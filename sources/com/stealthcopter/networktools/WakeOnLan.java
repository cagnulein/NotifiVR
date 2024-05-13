package com.stealthcopter.networktools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class WakeOnLan {
    public static final int DEFAULT_NO_PACKETS = 5;
    public static final int DEFAULT_PORT = 9;
    public static final int DEFAULT_TIMEOUT_MILLIS = 10000;
    private InetAddress inetAddress;
    private String ipStr;
    private String macStr;
    private int noPackets = 5;
    private int port = 9;
    private int timeoutMillis = DEFAULT_TIMEOUT_MILLIS;

    public interface WakeOnLanListener {
        void onError(Exception exc);

        void onSuccess();
    }

    private WakeOnLan() {
    }

    public static WakeOnLan onIp(String str) {
        WakeOnLan wakeOnLan = new WakeOnLan();
        wakeOnLan.ipStr = str;
        return wakeOnLan;
    }

    public static WakeOnLan onAddress(InetAddress inetAddress2) {
        WakeOnLan wakeOnLan = new WakeOnLan();
        wakeOnLan.inetAddress = inetAddress2;
        return wakeOnLan;
    }

    public WakeOnLan withMACAddress(String str) {
        if (str != null) {
            this.macStr = str;
            return this;
        }
        throw new NullPointerException("MAC Cannot be null");
    }

    public WakeOnLan setPort(int i) {
        if (i <= 0 || i > 65535) {
            throw new IllegalArgumentException("Invalid port " + i);
        }
        this.port = i;
        return this;
    }

    public WakeOnLan setNoPackets(int i) {
        if (i > 0) {
            this.noPackets = i;
            return this;
        }
        throw new IllegalArgumentException("Invalid number of packets to send " + i);
    }

    public WakeOnLan setTimeout(int i) {
        if (i > 0) {
            this.timeoutMillis = i;
            return this;
        }
        throw new IllegalArgumentException("Timeout cannot be less than zero");
    }

    public void wake() throws IOException {
        if (this.ipStr == null && this.inetAddress == null) {
            throw new IllegalArgumentException("You must declare ip address or supply an inetaddress");
        }
        String str = this.macStr;
        if (str != null) {
            String str2 = this.ipStr;
            if (str2 != null) {
                sendWakeOnLan(str2, str, this.port, this.timeoutMillis, this.noPackets);
            } else {
                sendWakeOnLan(this.inetAddress, str, this.port, this.timeoutMillis, this.noPackets);
            }
        } else {
            throw new NullPointerException("You did not supply a mac address with withMac(...)");
        }
    }

    public void wake(final WakeOnLanListener wakeOnLanListener) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    WakeOnLan.this.wake();
                    if (wakeOnLanListener != null) {
                        wakeOnLanListener.onSuccess();
                    }
                } catch (IOException e) {
                    WakeOnLanListener wakeOnLanListener = wakeOnLanListener;
                    if (wakeOnLanListener != null) {
                        wakeOnLanListener.onError(e);
                    }
                }
            }
        }).start();
    }

    public static void sendWakeOnLan(String str, String str2) throws IllegalArgumentException, IOException {
        sendWakeOnLan(str, str2, 9, (int) DEFAULT_TIMEOUT_MILLIS, 5);
    }

    public static void sendWakeOnLan(String str, String str2, int i, int i2, int i3) throws IllegalArgumentException, IOException {
        if (str != null) {
            sendWakeOnLan(InetAddress.getByName(str), str2, i, i2, i3);
            return;
        }
        throw new IllegalArgumentException("Address cannot be null");
    }

    public static void sendWakeOnLan(InetAddress inetAddress2, String str, int i, int i2, int i3) throws IllegalArgumentException, IOException {
        if (inetAddress2 == null) {
            throw new IllegalArgumentException("Address cannot be null");
        } else if (str == null) {
            throw new IllegalArgumentException("MAC Address cannot be null");
        } else if (i <= 0 || i > 65535) {
            throw new IllegalArgumentException("Invalid port " + i);
        } else if (i3 > 0) {
            byte[] macBytes = MACTools.getMacBytes(str);
            byte[] bArr = new byte[((macBytes.length * 16) + 6)];
            for (int i4 = 0; i4 < 6; i4++) {
                bArr[i4] = -1;
            }
            for (int i5 = 6; i5 < bArr.length; i5 += macBytes.length) {
                System.arraycopy(macBytes, 0, bArr, i5, macBytes.length);
            }
            DatagramPacket datagramPacket = new DatagramPacket(bArr, bArr.length, inetAddress2, i);
            for (int i6 = 0; i6 < i3; i6++) {
                DatagramSocket datagramSocket = new DatagramSocket();
                datagramSocket.setSoTimeout(i2);
                datagramSocket.send(datagramPacket);
                datagramSocket.close();
            }
        } else {
            throw new IllegalArgumentException("Invalid number of packets to send " + i3);
        }
    }
}
