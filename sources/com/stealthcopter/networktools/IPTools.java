package com.stealthcopter.networktools;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class IPTools {
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");
    private static final Pattern IPV6_STD_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    private IPTools() {
    }

    public static boolean isIPv4Address(String str) {
        return str != null && IPV4_PATTERN.matcher(str).matches();
    }

    public static boolean isIPv6StdAddress(String str) {
        return str != null && IPV6_STD_PATTERN.matcher(str).matches();
    }

    public static boolean isIPv6HexCompressedAddress(String str) {
        return str != null && IPV6_HEX_COMPRESSED_PATTERN.matcher(str).matches();
    }

    public static boolean isIPv6Address(String str) {
        return str != null && (isIPv6StdAddress(str) || isIPv6HexCompressedAddress(str));
    }

    public static InetAddress getLocalIPv4Address() {
        ArrayList<InetAddress> localIPv4Addresses = getLocalIPv4Addresses();
        if (localIPv4Addresses.size() > 0) {
            return localIPv4Addresses.get(0);
        }
        return null;
    }

    public static ArrayList<InetAddress> getLocalIPv4Addresses() {
        ArrayList<InetAddress> arrayList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress nextElement = inetAddresses.nextElement();
                    if ((nextElement instanceof Inet4Address) && !nextElement.isLoopbackAddress()) {
                        arrayList.add(nextElement);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static boolean isIpAddressLocalhost(InetAddress inetAddress) {
        if (inetAddress == null) {
            return false;
        }
        if (inetAddress.isAnyLocalAddress() || inetAddress.isLoopbackAddress()) {
            return true;
        }
        try {
            if (NetworkInterface.getByInetAddress(inetAddress) != null) {
                return true;
            }
            return false;
        } catch (SocketException unused) {
            return false;
        }
    }

    public static boolean isIpAddressLocalNetwork(InetAddress inetAddress) {
        return inetAddress != null && inetAddress.isSiteLocalAddress();
    }
}
