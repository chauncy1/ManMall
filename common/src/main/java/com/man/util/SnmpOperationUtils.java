package com.man.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class SnmpOperationUtils {

    private static Logger log = LoggerFactory.getLogger(SnmpOperationUtils.class);

    /**
     *
     * @throws IOException
     */
    public static Snmp getSnmpInstance() throws IOException {
        //1、初始化多线程消息转发类
        MessageDispatcher messageDispatcher = new MessageDispatcherImpl();
        //其中要增加三种处理模型。如果snmp初始化使用的是Snmp(TransportMapping<? extends Address> transportMapping) ,就不需要增加
        messageDispatcher.addMessageProcessingModel(new MPv1());
        messageDispatcher.addMessageProcessingModel(new MPv2c());
        //当要支持snmpV3版本时，需要配置user
//        OctetString localEngineID = new OctetString(MPv3.createLocalEngineID());
//        USM usm = new USM(SecurityProtocols.getInstance().addDefaultProtocols(), localEngineID, 0);
//
//        OctetString userName1 = new OctetString(username);
//        OctetString authPass = new OctetString(authPassword);
//        OctetString privPass = new OctetString(privPassword);
//        UsmUser user = new UsmUser(userName1, AuthMD5.ID, authPass, PrivAES128.ID, privPass);
//
//        usm.addUser(user.getSecurityName(), user);
//        messageDispatcher.addMessageProcessingModel(new MPv3(usm));
        //2、创建transportMapping  ip为本地ip，可以不设置
        //UdpAddress updAddr = (UdpAddress) GenericAddress.parse("udp:192.168.22.100/161");
        TransportMapping<?> transportMapping = new DefaultUdpTransportMapping();
        //3、正式创建snmp
        Snmp snmp = new Snmp(messageDispatcher, transportMapping);
        //开启监听
        snmp.listen();
        return snmp;

    }

    /**
     *
     * @param version 0, 1, 3
     * @param community 默认private 和 public
     * @param targetAddress 格式：udp:192.168.10.1/161
     * @return
     */
    public static Target createTarget(int version, String community, String targetAddress) {
        Target target = null;
        if (!(version == SnmpConstants.version3 || version == SnmpConstants.version2c || version == SnmpConstants.version1)) {
            //log.error("参数version异常");
            return target;
        }
        if (version == SnmpConstants.version3) {
            target = new UserTarget();
            //snmpV3需要设置安全级别和安全名称，其中安全名称是创建snmp指定user设置的new OctetString("SNMPV3")
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            target.setSecurityName(new OctetString(community));
        } else {
            //snmpV1和snmpV2需要指定团体名名称
            target = new CommunityTarget();
            ((CommunityTarget) target).setCommunity(new OctetString(community));
            if (version == SnmpConstants.version2c) {
                target.setSecurityModel(SecurityModel.SECURITY_MODEL_SNMPv2c);
            }
        }
        target.setVersion(version);
        //必须指定，没有设置就会报错。
        target.setAddress(GenericAddress.parse(targetAddress));
        target.setRetries(2);
        target.setTimeout(2000);
        return target;
    }


    /**
     *
     * @param version snmp 版本 ： 0，1，3
     * @param type org.snmp4j.PDU
     * @param targetOid mib结点值
     * @return
     */
    public static PDU createPDU(int version, int type, String targetOid, Object val) throws UnsupportedEncodingException {
        PDU pdu = null;
        if (version == SnmpConstants.version3) {
            pdu = new ScopedPDU();
        } else {
            pdu = new PDUv1();
        }
        pdu.setType(type);
        if(null != val){
            if (val instanceof String) {
                String value = (String) val;
                pdu.add(new VariableBinding(new OID(targetOid), new OctetString(value.getBytes(StandardCharsets.UTF_8))));
            } else if (val instanceof Integer) {
                pdu.add(new VariableBinding(new OID(targetOid), new Integer32(Integer.parseInt(val + ""))));
            } else {
                pdu.add(new VariableBinding(new OID(targetOid), new OctetString(val.toString().getBytes(StandardCharsets.UTF_8))));
            }
        }else{
            pdu.add(new VariableBinding(new OID(targetOid)));
        }
        return pdu;
    }

    public static void close(TransportMapping<?> transport, Snmp snmp) {
        try {
            if (transport != null) {
                transport.close();
            }
            if (snmp != null) {
                snmp.close();
            }
        } catch (IOException e) {
            log.error("Close snmp error: ", e);
        }
    }


    public static String decimalToHexaDecimalMac(String decimalMac, String macSeparator){
        String[] macUnitArr = decimalMac.split(macSeparator);
        if(macUnitArr.length != 6){
            return decimalMac;
        }
        StringBuilder hexaDecimal = new StringBuilder();
        for(String macUnit : macUnitArr){
            String hexMacUnit = Integer.toHexString(Integer.parseInt(macUnit));
            // 一位的数字补个0。60:f2:ef:5:5a:8b --> 60:f2:ef:05:5a:8b
            hexaDecimal.append(hexMacUnit.length() < 2 ? ("0" + hexMacUnit) : hexMacUnit).append(":");
        }
        return hexaDecimal.toString().substring(0, hexaDecimal.length() - 1);
    }

}
