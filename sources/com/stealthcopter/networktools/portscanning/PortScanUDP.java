package com.stealthcopter.networktools.portscanning;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class PortScanUDP {
    private PortScanUDP() {
    }

    public static boolean scanAddress(InetAddress inetAddress, int i, int i2) {
        try {
            byte[] bArr = new byte[128];
            DatagramPacket datagramPacket = new DatagramPacket(bArr, bArr.length);
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(i2);
            datagramSocket.connect(inetAddress, i);
            datagramSocket.send(datagramPacket);
            datagramSocket.isConnected();
            datagramSocket.receive(datagramPacket);
            datagramSocket.close();
            return false;
        } catch (SocketTimeoutException unused) {
            return true;
        } catch (Exception unused2) {
            return false;
        }
    }
}
