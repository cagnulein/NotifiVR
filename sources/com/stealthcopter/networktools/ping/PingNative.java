package com.stealthcopter.networktools.ping;

import com.stealthcopter.networktools.IPTools;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class PingNative {
    private PingNative() {
    }

    public static PingResult ping(InetAddress inetAddress, PingOptions pingOptions) throws IOException, InterruptedException {
        PingResult pingResult = new PingResult(inetAddress);
        if (inetAddress == null) {
            pingResult.isReachable = false;
            return pingResult;
        }
        StringBuilder sb = new StringBuilder();
        Runtime runtime = Runtime.getRuntime();
        int max = Math.max(pingOptions.getTimeoutMillis() / 1000, 1);
        int max2 = Math.max(pingOptions.getTimeToLive(), 1);
        String hostAddress = inetAddress.getHostAddress();
        String str = "ping";
        if (hostAddress == null) {
            hostAddress = inetAddress.getHostName();
        } else if (IPTools.isIPv6Address(hostAddress)) {
            str = "ping6";
        } else {
            IPTools.isIPv4Address(hostAddress);
        }
        Process exec = runtime.exec(str + " -c 1 -W " + max + " -t " + max2 + " " + hostAddress);
        exec.waitFor();
        int exitValue = exec.exitValue();
        if (exitValue != 0) {
            pingResult.error = exitValue != 1 ? "error, exit = 2" : "failed, exit = 1";
            return pingResult;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return getPingStats(pingResult, sb.toString());
            }
            sb.append(readLine);
            sb.append("\n");
        }
    }

    public static PingResult getPingStats(PingResult pingResult, String str) {
        String str2 = "unknown host";
        if (str.contains("0% packet loss")) {
            int indexOf = str.indexOf("/mdev = ");
            int indexOf2 = str.indexOf(" ms\n", indexOf);
            pingResult.fullString = str;
            if (indexOf == -1 || indexOf2 == -1) {
                str2 = "Error: " + str;
            } else {
                String substring = str.substring(indexOf + 8, indexOf2);
                String[] split = substring.split("/");
                pingResult.isReachable = true;
                pingResult.result = substring;
                pingResult.timeTaken = Float.parseFloat(split[1]);
                return pingResult;
            }
        } else if (str.contains("100% packet loss")) {
            str2 = "100% packet loss";
        } else if (str.contains("% packet loss")) {
            str2 = "partial packet loss";
        } else if (!str.contains(str2)) {
            str2 = "unknown error in getPingStats";
        }
        pingResult.error = str2;
        return pingResult;
    }
}
