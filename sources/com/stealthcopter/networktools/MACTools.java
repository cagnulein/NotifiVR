package com.stealthcopter.networktools;

import java.util.regex.Pattern;

public class MACTools {
    private static final Pattern PATTERN_MAC = Pattern.compile("^([0-9A-Fa-f]{2}[\\.:-]){5}([0-9A-Fa-f]{2})$");

    private MACTools() {
    }

    public static boolean isValidMACAddress(String str) {
        return str != null && PATTERN_MAC.matcher(str).matches();
    }

    public static byte[] getMacBytes(String str) throws IllegalArgumentException {
        if (str != null) {
            byte[] bArr = new byte[6];
            String[] split = str.split("(\\:|\\-)");
            if (split.length == 6) {
                int i = 0;
                while (i < 6) {
                    try {
                        bArr[i] = (byte) Integer.parseInt(split[i], 16);
                        i++;
                    } catch (NumberFormatException unused) {
                        throw new IllegalArgumentException("Invalid hex digit in MAC address.");
                    }
                }
                return bArr;
            }
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        throw new IllegalArgumentException("Mac Address cannot be null");
    }
}
