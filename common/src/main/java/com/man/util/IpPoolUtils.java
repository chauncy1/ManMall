package com.man.util;

import cn.hutool.core.util.StrUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @description: ip段工具类
 * @author: mchangx
 * @email: mancx95c@visionvera.com
 * @date: 2022/3/22 15:38
 */
public class IpPoolUtils {

    /**
     * 在main方法里面可以测试下
     * 主要功能都在main方法里，需要什么自己找
     */
    public static void main(String[] args) {
        String ip = "172.31.0.0";//ip
        String mask = "16";//位数，如果只知道子网掩码不知道位数的话在参考getMaskMap()方法

        //获得起始IP和终止IP的方法（包含网络地址和广播地址）
        String startIp = getBeginIpStr(ip, mask);
        String endIp = getEndIpStr(ip, mask);
        System.out.println("起始IP：" + startIp + "终止IP：" + endIp);

        //获得起始IP和终止IP的方法（不包含网络地址和广播地址）
        String subStart = startIp.split("\\.")[0] + "." + startIp.split("\\.")[1] + "." + startIp.split("\\.")[2] + ".";
        String subEnd = endIp.split("\\.")[0] + "." + endIp.split("\\.")[1] + "." + endIp.split("\\.")[2] + ".";
        startIp = subStart + (Integer.parseInt(startIp.split("\\.")[3]) + 1);
        endIp = subEnd + (Integer.parseInt(endIp.split("\\.")[3]) - 1);
        System.out.println("起始IP：" + startIp + "终止IP：" + endIp);

        //判断一个IP是否属于某个网段
        boolean flag = isInRange("10.2.0.0", "10.3.0.0/17");
        System.out.println(flag);

        //根据位数查询IP数量
        int ipCount = getIpCount("8");
        System.out.println(ipCount);

        //判断是否是一个IP
        System.out.println(isIp("192.168.1.0"));

        //把ip转换为数字(mysql中inet_aton()的实现)
        System.out.println(ipToDouble("192.168.1.1"));

        //打印IP段所有IP（IP过多会内存溢出）
        List<String> list = parseIpMaskRange(ip, mask);
        for (String s : list) {
            System.out.println(s);
        }
    }


    /**
     * 功能：判断一个IP是不是在一个网段下的
     * 格式：isInRange("192.168.8.3", "192.168.9.10/22");
     */
    public static boolean isInRange(String ip, String cidr) {
        String[] ips = ip.split("\\.");
        int ipAddr = (Integer.parseInt(ips[0]) << 24)
                | (Integer.parseInt(ips[1]) << 16)
                | (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
        int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
        int mask = 0xFFFFFFFF << (32 - type);
        String cidrIp = cidr.replaceAll("/.*", "");
        String[] cidrIps = cidrIp.split("\\.");
        int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24)
                | (Integer.parseInt(cidrIps[1]) << 16)
                | (Integer.parseInt(cidrIps[2]) << 8)
                | Integer.parseInt(cidrIps[3]);

        return (ipAddr & mask) == (cidrIpAddr & mask);
    }

    /**
     * 功能：根据IP和位数返回该IP网段的所有IP
     * 格式：parseIpMaskRange("192.192.192.1.", "23")
     */
    public static List<String> parseIpMaskRange(String ip,String mask){
        List<String> list=new ArrayList<>();
        if ("32".equals(mask)) {
            list.add(ip);
        }else{
            String startIp=getBeginIpStr(ip, mask);
            String endIp=getEndIpStr(ip, mask);
            if (!"31".equals(mask)) {
                String subStart=startIp.split("\\.")[0]+"."+startIp.split("\\.")[1]+"."+startIp.split("\\.")[2]+".";
                String subEnd=endIp.split("\\.")[0]+"."+endIp.split("\\.")[1]+"."+endIp.split("\\.")[2]+".";
                startIp=subStart+(Integer.parseInt(startIp.split("\\.")[3])+1);
                endIp=subEnd+(Integer.parseInt(endIp.split("\\.")[3])-1);
            }
            list=parseIpRange(startIp, endIp);
        }
        return list;
    }

    /**
     * 功能：根据位数返回IP总数
     * 格式：parseIpMaskRange("192.192.192.1", "23")
     */
    public static int getIpCount(String mask) {
        //IP总数，去小数点
        return BigDecimal.valueOf(Math.pow(2, 32 - Integer.parseInt(mask))).setScale(0, BigDecimal.ROUND_DOWN).intValue();
    }

    /**
     * 功能：根据位数返回IP总数
     * 格式：isIP("192.192.192.1")
     */
    public static boolean isIp(String str) {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(str).matches();
    }

    public static List<String> parseIpRange(String ipfrom, String ipto) {
        List<String> ips = new ArrayList<String>();
        String[] ipfromd = ipfrom.split("\\.");
        String[] iptod = ipto.split("\\.");
        int[] intIpf = new int[4];
        int[] intIpt = new int[4];
        for (int i = 0; i < 4; i++) {
            intIpf[i] = Integer.parseInt(ipfromd[i]);
            intIpt[i] = Integer.parseInt(iptod[i]);
        }
        for (int A = intIpf[0]; A <= intIpt[0]; A++) {
            for (int B = (A == intIpf[0] ? intIpf[1] : 0); B <= (A == intIpt[0] ? intIpt[1] : 255); B++) {
                for (int C = (B == intIpf[1] ? intIpf[2] : 0); C <= (B == intIpt[1] ? intIpt[2] : 255); C++) {
                    for (int D = (C == intIpf[2] ? intIpf[3] : 0); D <= (C == intIpt[2] ? intIpt[3] : 255); D++) {
                        ips.add(A + "." + B + "." + C + "." + D);
                    }
                }
            }
        }
        return ips;
    }

    /**
     * 把long类型的Ip转为一般Ip类型：xx.xx.xx.xx
     *
     * @param ip
     * @return
     */
    public static String getIpFromLong(Long ip)
    {
        String s1 = String.valueOf((ip & 4278190080L) / 16777216L);
        String s2 = String.valueOf((ip & 16711680L) / 65536L);
        String s3 = String.valueOf((ip & 65280L) / 256L);
        String s4 = String.valueOf(ip & 255L);
        return s1 + "." + s2 + "." + s3 + "." + s4;
    }
    /**
     * 把xx.xx.xx.xx类型的转为long类型的
     *
     * @param ip
     * @return
     */
    public static Long getIpFromString(String ip)
    {
        long ipLong = 0L;
        String ipTemp = ip;
        ipLong = ipLong * 256
                + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf('.')));
        ipTemp = ipTemp.substring(ipTemp.indexOf('.') + 1, ipTemp.length());
        ipLong = ipLong * 256
                + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf('.')));
        ipTemp = ipTemp.substring(ipTemp.indexOf(".") + 1, ipTemp.length());
        ipLong = ipLong * 256
                + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf('.')));
        ipTemp = ipTemp.substring(ipTemp.indexOf('.') + 1, ipTemp.length());
        ipLong = ipLong * 256 + Long.parseLong(ipTemp);
        return ipLong;
    }
    /**
     * 根据掩码位获取掩码
     *
     * @param maskBit
     *            掩码位数，如"28"、"30"
     * @return
     */
    public static String getMaskByMaskBit(String maskBit)
    {
        return "".equals(maskBit) ? "error, maskBit is null !" : getMaskMap(maskBit);
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP 如 IP串 218.240.38.69/30
     *
     * @param ip
     *            给定的IP，如218.240.38.69
     * @param maskBit
     *            给定的掩码位，如30
     * @return 起始IP的字符串表示
     */
    public static String  getBeginIpStr(String ip, String maskBit)
    {
        return getIpFromLong(getBeginIpLong(ip, maskBit));
    }
    /**
     * 根据 ip/掩码位 计算IP段的起始IP 如 IP串 218.240.38.69/30
     *
     * @param ip
     *            给定的IP，如218.240.38.69
     * @param maskBit
     *            给定的掩码位，如30
     * @return 起始IP的长整型表示
     */
    public static Long getBeginIpLong(String ip, String maskBit)
    {
        return getIpFromString(ip) & getIpFromString(getMaskByMaskBit(maskBit));
    }
    /**
     * 根据 ip/掩码位 计算IP段的终止IP 如 IP串 218.240.38.69/30
     *
     * @param ip
     *            给定的IP，如218.240.38.69
     * @param maskBit
     *            给定的掩码位，如30
     * @return 终止IP的字符串表示
     */
    public static String getEndIpStr(String ip, String maskBit)
    {
        return getIpFromLong(getEndIpLong(ip, maskBit));
    }

    /**
     * 根据 ip/掩码位 计算IP段的终止IP 如 IP串 218.240.38.69/30
     *
     * @param ip
     *            给定的IP，如218.240.38.69
     * @param maskBit
     *            给定的掩码位，如30
     * @return 终止IP的长整型表示
     */
    public static Long getEndIpLong(String ip, String maskBit)
    {
        return getBeginIpLong(ip, maskBit)
                + ~getIpFromString(getMaskByMaskBit(maskBit));
    }


    /**
     * 根据子网掩码转换为掩码位 如 255.255.255.252转换为掩码位 为 30
     *
     * @return 掩码位
     */
    public static int getNetMask(String netmarks)
    {
        StringBuilder sbf;
        String str;
        int inetmask = 0;
        int count = 0;
        String[] ipList = netmarks.split("\\.");
        for (String s : ipList) {
            sbf = toBin(Integer.parseInt(s));
            str = sbf.reverse().toString();
            count = 0;
            for (int i = 0; i < str.length(); i++) {
                i = str.indexOf('1', i);
                if (i == -1) {
                    break;
                }
                count++;
            }
            inetmask += count;
        }
        return inetmask;
    }

    /**
     * 计算子网大小
     *
     * @param maskBit
     *            掩码位
     * @return 地址池大小
     */
    public static int getPoolMax(int maskBit) {
        int count = (int) Math.pow(2, 32 - maskBit);
        return maskBit <= 30 ? count - 2 : count;
    }
    private static StringBuilder toBin(int x)
    {
        StringBuilder result = new StringBuilder();
        result.append(x % 2);
        x /= 2;
        while (x > 0)
        {
            result.append(x % 2);
            x /= 2;
        }
        return result;
    }

    public static String getMaskMap(String maskBit) {
        if ("1".equals(maskBit)) {
            return "128.0.0.0";
        }
        if ("2".equals(maskBit)) {
            return "192.0.0.0";
        }
        if ("3".equals(maskBit)) {
            return "224.0.0.0";
        }
        if ("4".equals(maskBit)) {
            return "240.0.0.0";
        }
        if ("5".equals(maskBit)) {
            return "248.0.0.0";
        }
        if ("6".equals(maskBit)) {
            return "252.0.0.0";
        }
        if ("7".equals(maskBit)) {
            return "254.0.0.0";
        }
        if ("8".equals(maskBit)) {
            return "255.0.0.0";
        }
        if ("9".equals(maskBit)) {
            return "255.128.0.0";
        }
        if ("10".equals(maskBit)) {
            return "255.192.0.0";
        }
        if ("11".equals(maskBit)) {
            return "255.224.0.0";
        }
        if ("12".equals(maskBit)) {
            return "255.240.0.0";
        }
        if ("13".equals(maskBit)) {
            return "255.248.0.0";
        }
        if ("14".equals(maskBit)) {
            return "255.252.0.0";
        }
        if ("15".equals(maskBit)) {
            return "255.254.0.0";
        }
        if ("16".equals(maskBit)) {
            return "255.255.0.0";
        }
        if ("17".equals(maskBit)) {
            return "255.255.128.0";
        }
        if ("18".equals(maskBit)) {
            return "255.255.192.0";
        }
        if ("19".equals(maskBit)) {
            return "255.255.224.0";
        }
        if ("20".equals(maskBit)) {
            return "255.255.240.0";
        }
        if ("21".equals(maskBit)) {
            return "255.255.248.0";
        }
        if ("22".equals(maskBit)) {
            return "255.255.252.0";
        }
        if ("23".equals(maskBit)) {
            return "255.255.254.0";
        }
        if ("24".equals(maskBit)) {
            return "255.255.255.0";
        }
        if ("25".equals(maskBit)) {
            return "255.255.255.128";
        }
        if ("26".equals(maskBit)) {
            return "255.255.255.192";
        }
        if ("27".equals(maskBit)) {
            return "255.255.255.224";
        }
        if ("28".equals(maskBit)) {
            return "255.255.255.240";
        }
        if ("29".equals(maskBit)) {
            return "255.255.255.248";
        }
        if ("30".equals(maskBit)) {
            return "255.255.255.252";
        }
        if ("31".equals(maskBit)) {
            return "255.255.255.254";
        }
        if ("32".equals(maskBit)) {
            return "255.255.255.255";
        }
        return "-1";
    }

    public static double ipToDouble(String ip) {
        String[] arr = ip.split("\\.");
        double d1 = Double.parseDouble(arr[0]);
        double d2 = Double.parseDouble(arr[1]);
        double d3 = Double.parseDouble(arr[2]);
        double d4 = Double.parseDouble(arr[3]);
        return d1 * Math.pow(256, 3) + d2 * Math.pow(256, 2) + d3 * 256 + d4;
    }

    /**
     *
     * @param ipSection 192.168.1.0-192.168.1.254
     * @param ip 192.168.1.21
     * @return
     */
    public static boolean ipIsValid(String ipSection, String ip) {
        if (ipSection == null) {
            throw new NullPointerException("IP段不能为空！");
        }
        if (ip == null) {
            throw new NullPointerException("IP不能为空！");
        }
        ipSection = ipSection.trim();
        ip = ip.trim();
        final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
        final String REGX_IPB = REGX_IP + "\\-" + REGX_IP;
        if (!ipSection.matches(REGX_IPB) || !ip.matches(REGX_IP))
            return false;
        int idx = ipSection.indexOf('-');
        String[] sips = ipSection.substring(0, idx).split("\\.");
        String[] sipe = ipSection.substring(idx + 1).split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;
        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }
        if (ips > ipe) {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe;
    }
}