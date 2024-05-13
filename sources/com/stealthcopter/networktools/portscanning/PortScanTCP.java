package com.stealthcopter.networktools.portscanning;

public class PortScanTCP {
    private PortScanTCP() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x001e A[SYNTHETIC, Splitter:B:15:0x001e] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x002a A[SYNTHETIC, Splitter:B:24:0x002a] */
    /* JADX WARNING: Removed duplicated region for block: B:29:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean scanAddress(java.net.InetAddress r2, int r3, int r4) {
        /*
            r0 = 0
            java.net.Socket r1 = new java.net.Socket     // Catch:{ IOException -> 0x0027, all -> 0x001a }
            r1.<init>()     // Catch:{ IOException -> 0x0027, all -> 0x001a }
            java.net.InetSocketAddress r0 = new java.net.InetSocketAddress     // Catch:{ IOException -> 0x0028, all -> 0x0018 }
            r0.<init>(r2, r3)     // Catch:{ IOException -> 0x0028, all -> 0x0018 }
            r1.connect(r0, r4)     // Catch:{ IOException -> 0x0028, all -> 0x0018 }
            r2 = 1
            r1.close()     // Catch:{ IOException -> 0x0013 }
            goto L_0x0017
        L_0x0013:
            r3 = move-exception
            r3.printStackTrace()
        L_0x0017:
            return r2
        L_0x0018:
            r2 = move-exception
            goto L_0x001c
        L_0x001a:
            r2 = move-exception
            r1 = r0
        L_0x001c:
            if (r1 == 0) goto L_0x0026
            r1.close()     // Catch:{ IOException -> 0x0022 }
            goto L_0x0026
        L_0x0022:
            r3 = move-exception
            r3.printStackTrace()
        L_0x0026:
            throw r2
        L_0x0027:
            r1 = r0
        L_0x0028:
            if (r1 == 0) goto L_0x0032
            r1.close()     // Catch:{ IOException -> 0x002e }
            goto L_0x0032
        L_0x002e:
            r2 = move-exception
            r2.printStackTrace()
        L_0x0032:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.stealthcopter.networktools.portscanning.PortScanTCP.scanAddress(java.net.InetAddress, int, int):boolean");
    }
}
