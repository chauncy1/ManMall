package com.man.service;

import com.man.common.result.CommonResult;
import com.man.util.SnmpOperationUtils;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SnmpOperationService {

    /**
     * @Author mchangx
     * @Description WALK方式请求
     * @Date 11:26 2022/11/8
     * @Param [oid, version, community, targetAddress]
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
    public Map<String, String> snmpWalk(String oid, int version, String community, String targetAddress) throws IOException {
        Map<String, String> map = new HashMap<>();
        Snmp snmp = null;
        try{
            //1、初始化snmp,并开启监听
            snmp = SnmpOperationUtils.getSnmpInstance();
            //2、创建目标对象
            Target target = SnmpOperationUtils.createTarget(version, community, targetAddress);
            //3、创建报文
            PDU pdu = SnmpOperationUtils.createPDU(version, PDU.GETNEXT, oid, null) ;
            //4、发送报文，并获取返回结果
            boolean matched = true;
            while (matched) {
                ResponseEvent responseEvent = snmp.send(pdu, target);
                if (responseEvent == null || responseEvent.getResponse() == null) {
                    log.warn("Snmp请求超时，请检查配置或交换机状态，地址：{}，oid：{}", targetAddress, oid);
                    break;
                }
                PDU responsePdu = responseEvent.getResponse();
                String nextOid = null;
                List<? extends VariableBinding> variableBindings = responsePdu.getVariableBindings();
                for (VariableBinding variableBinding : variableBindings) {
                    Variable variable = variableBinding.getVariable();
                    // 检查walk是否结束
                    if (checkWalkFinished(new OID(oid), responsePdu, variableBinding)) {
                        matched = false;
                        break;
                    }
                    nextOid = variableBinding.getOid().toDottedString();
                    map.put(nextOid.replace(oid + ".", ""), String.valueOf(variable));
                }
                if (!matched) {
                    break;
                }
                pdu.clear();
                pdu.add(new VariableBinding(new OID(nextOid)));
            }
        }catch (Exception e){
            log.error("Snmp walk报错, targetAddress=" + targetAddress);
            throw e;
        }finally {
            SnmpOperationUtils.close(null, snmp);
        }
        return map;
    }

    /**
     *
     * @param oid
     * @param version
     * @param community
     * @param targetAddress
     * @return Map<String, Object>:
     *          key: String, 交换机节点index,
     *          value : Long[], 大小为2：1为index对应数据，2为时间戳
     * @throws IOException
     */
    public Map<String, Object> snmpWalkWithTimeStamp(String oid, int version, String community, String targetAddress) throws IOException {
        Map<String, Object> map = new HashMap<>();
        Snmp snmp = null;
        try{
            //1、初始化snmp,并开启监听
            snmp = SnmpOperationUtils.getSnmpInstance();
            //2、创建目标对象
            Target target = SnmpOperationUtils.createTarget(version, community, targetAddress);
            //3、创建报文
            PDU pdu = SnmpOperationUtils.createPDU(version, PDU.GETNEXT, oid, null);
            //4、发送报文，并获取返回结果
            boolean matched = true;
            while (matched) {
                ResponseEvent responseEvent = snmp.send(pdu, target);
                if (responseEvent == null || responseEvent.getResponse() == null) {
                    log.warn("Snmp请求超时，请检查配置或交换机状态，{}，oid：{}", targetAddress, oid);
                    break;
                }
                PDU responsePdu = responseEvent.getResponse();
                String nextOid = null;
                List<? extends VariableBinding> variableBindings = responsePdu.getVariableBindings();
                for (VariableBinding variableBinding : variableBindings) {
                    Variable variable = variableBinding.getVariable();
                    //如果不是这个节点下的oid则终止遍历，否则会直到整个遍历完。
                    if (checkWalkFinished(new OID(oid), responsePdu, variableBinding)) {
                        matched = false;
                        break;
                    }
                    nextOid = variableBinding.getOid().toDottedString();
                    map.put(nextOid.replace(oid + ".", ""), new Long[]{Long.valueOf(String.valueOf(variable)), System.currentTimeMillis()});
                }
                if (!matched) {
                    break;
                }
                pdu.clear();
                pdu.add(new VariableBinding(new OID(nextOid)));
            }
        }finally {
//            stringRedisTemplate.delete(H3CConstants.REDIS_KEY_MONITOR_SNMP_TIMEOUT + Thread.currentThread().getId());
            SnmpOperationUtils.close(null, snmp);
        }
        return map;
    }


    public CommonResult<String> snmpSet(String oid, int val, int version, String community, String targetAddress) throws IOException {
        Map<String, String> map = new HashMap<>();
        Snmp snmp = null;
        try{
            //1、初始化snmp,并开启监听
            snmp = SnmpOperationUtils.getSnmpInstance();
            //2、创建目标对象
            Target target = SnmpOperationUtils.createTarget(version, community, targetAddress);
            //3、创建报文
            PDU pdu = SnmpOperationUtils.createPDU(version, PDU.SET, oid, val);
            //4、发送报文，并获取返回结果
            ResponseEvent responseEvent = snmp.send(pdu, target);
            if (null == responseEvent || null == responseEvent.getResponse()) {
                log.warn("Snmp请求超时，请检查配置或交换机状态，{}，oid：{}", targetAddress, oid);
                return CommonResult.failed("Snmp request timeOut...");
            } else {
                PDU responsePdu = responseEvent.getResponse();
                if (responsePdu.getErrorStatus() == PDU.noError) {
                    //get方式获取到的返回值
                    List<? extends VariableBinding> vbs = responsePdu.getVariableBindings();
                    for (VariableBinding vb : vbs) {
                        map.put(String.valueOf(vb.getOid()), String.valueOf(vb.getVariable()));
                    }
                } else {
                    log.error("Error:" + responsePdu.getErrorStatusText());
                    return CommonResult.failed("Error:" + responsePdu.getErrorStatusText());
                }
            }
        }finally {
            SnmpOperationUtils.close(null, snmp);
        }
        return CommonResult.success(null, "华三交换机端口使能状态配置成功");
    }


    /**
     * @Author mchangx
     * @Description 检查SNML walk是否结束
     * @Date 11:06 2022/11/8
     * @Param [walkOID, pdu, vb]
     * @return boolean
     **/
    private boolean checkWalkFinished(OID walkOID, PDU pdu, VariableBinding vb) {
        boolean finished = false;
        if (pdu.getErrorStatus() != 0) {
            log.debug("pdu.getErrorStatus() != 0 . oid=" + vb.getOid().toDottedString() + pdu.getErrorStatusText());
            finished = true;
        } else if (vb.getOid() == null) {
            finished = true;
        } else if (vb.getOid().size() < walkOID.size()) {
            // 子oid比目标oid还小，没见过这情况
            finished = true;
        } else if (walkOID.leftMostCompare(walkOID.size(), vb.getOid()) != 0) {
            // vb的oid前缀应该与walkOID相同。比如1.3.6.1.1（walkODID）和1.3.6.1.1.13（vb）
            // 不相同则说明此OID walk结束
            finished = true;
        } else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
            // walk结束标识或没有此对象标识或没有此实例标识
            finished = true;
        } else if (vb.getOid().compareTo(walkOID) <= 0) {
            // 平常情况应该与leftMostCompare哪个else if相同逻辑。
            finished = true;
        }
        return finished;
    }


}
