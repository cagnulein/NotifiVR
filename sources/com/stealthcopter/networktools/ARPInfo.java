package com.stealthcopter.networktools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ARPInfo {
    private ARPInfo() {
    }

    public static String getMACFromIPAddress(String str) {
        if (str == null) {
            return null;
        }
        Iterator<String> it = getLinesInARPCache().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            String[] split = it.next().split(" +");
            if (split.length >= 4 && str.equals(split[0])) {
                String str2 = split[3];
                if (str2.matches("..:..:..:..:..:..")) {
                    return str2;
                }
            }
        }
        return null;
    }

    public static String getIPAddressFromMAC(String str) {
        if (str == null) {
            return null;
        }
        if (str.matches("..:..:..:..:..:..")) {
            Iterator<String> it = getLinesInARPCache().iterator();
            while (it.hasNext()) {
                String[] split = it.next().split(" +");
                if (split.length >= 4 && str.equals(split[3])) {
                    return split[0];
                }
            }
            return null;
        }
        throw new IllegalArgumentException("Invalid MAC Address");
    }

    public static ArrayList<String> getAllIPAddressesInARPCache() {
        return new ArrayList<>(getAllIPAndMACAddressesInARPCache().keySet());
    }

    public static ArrayList<String> getAllMACAddressesInARPCache() {
        return new ArrayList<>(getAllIPAndMACAddressesInARPCache().values());
    }

    public static HashMap<String, String> getAllIPAndMACAddressesInARPCache() {
        HashMap<String, String> hashMap = new HashMap<>();
        Iterator<String> it = getLinesInARPCache().iterator();
        while (it.hasNext()) {
            String[] split = it.next().split(" +");
            if (split.length >= 4 && split[3].matches("..:..:..:..:..:..") && !split[3].equals("00:00:00:00:00:00")) {
                hashMap.put(split[0], split[3]);
            }
        }
        return hashMap;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0030 A[SYNTHETIC, Splitter:B:18:0x0030] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x003b A[SYNTHETIC, Splitter:B:24:0x003b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.util.ArrayList<java.lang.String> getLinesInARPCache() {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ Exception -> 0x002a }
            java.io.FileReader r3 = new java.io.FileReader     // Catch:{ Exception -> 0x002a }
            java.lang.String r4 = "/proc/net/arp"
            r3.<init>(r4)     // Catch:{ Exception -> 0x002a }
            r2.<init>(r3)     // Catch:{ Exception -> 0x002a }
        L_0x0012:
            java.lang.String r1 = r2.readLine()     // Catch:{ Exception -> 0x0023, all -> 0x0020 }
            if (r1 == 0) goto L_0x001c
            r0.add(r1)     // Catch:{ Exception -> 0x0023, all -> 0x0020 }
            goto L_0x0012
        L_0x001c:
            r2.close()     // Catch:{ IOException -> 0x0034 }
            goto L_0x0038
        L_0x0020:
            r0 = move-exception
            r1 = r2
            goto L_0x0039
        L_0x0023:
            r1 = move-exception
            r5 = r2
            r2 = r1
            r1 = r5
            goto L_0x002b
        L_0x0028:
            r0 = move-exception
            goto L_0x0039
        L_0x002a:
            r2 = move-exception
        L_0x002b:
            r2.printStackTrace()     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x0038
            r1.close()     // Catch:{ IOException -> 0x0034 }
            goto L_0x0038
        L_0x0034:
            r1 = move-exception
            r1.printStackTrace()
        L_0x0038:
            return r0
        L_0x0039:
            if (r1 == 0) goto L_0x0043
            r1.close()     // Catch:{ IOException -> 0x003f }
            goto L_0x0043
        L_0x003f:
            r1 = move-exception
            r1.printStackTrace()
        L_0x0043:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.stealthcopter.networktools.ARPInfo.getLinesInARPCache():java.util.ArrayList");
    }
}
