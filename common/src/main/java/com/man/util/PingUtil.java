package com.man.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: mchangx
 * @email: mancx95c@visionvera.com
 * @date: 2022/7/29 15:54
 */
@Slf4j
public class PingUtil {

    private static Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)", Pattern.CASE_INSENSITIVE);

    private static final int pingTimeOut = 3000;


    public static boolean ping(String ipAddress) throws Exception {
        return InetAddress.getByName(ipAddress).isReachable(pingTimeOut);
    }

    public static boolean ping(String ipAddress, int pingTimes, int timeOut) {
        BufferedReader in = null;
        Runtime r = Runtime.getRuntime();
        // 将要执行的ping命令,此命令是windows格式的命令
        String pingCommand = "ping " + ipAddress + " -n " + pingTimes + " -w " + timeOut;
        // Linux命令如下
        // String pingCommand = "ping" -c " + pingTimes + " -w " + timeOut + ipAddress;
        try {
            if (log.isDebugEnabled()) {
                log.debug(pingCommand);
            }
            // 执行命令并获取输出
            Process p = r.exec(pingCommand);
            if (p == null) {
                return false;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            int connectedCount = 0;
            String line;
            // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
            while ((line = in.readLine()) != null) {
                connectedCount += getCheckResult(line);
            }
            // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
            return connectedCount == pingTimes;
        } catch (Exception e) {
            log.error("执行ping命令异常", e);
            return false;
        } finally {
            if(Objects.nonNull(in)){
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("关闭输入流异常", e);
                }
            }
        }
    }


    //若line含有=18ms TTL=16字样,说明已经ping通,返回1,否則返回0.
    private static int getCheckResult(String line) { // System.out.println("控制台输出的结果为:"+line);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            return 1;
        }
        return 0;
    }

}
