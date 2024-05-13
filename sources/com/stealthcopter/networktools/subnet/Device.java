package com.stealthcopter.networktools.subnet;

import java.net.InetAddress;

public class Device {
    public String hostname;
    public String ip;
    public String mac;
    public float time = 0.0f;

    public Device(InetAddress inetAddress) {
        this.ip = inetAddress.getHostAddress();
        this.hostname = inetAddress.getCanonicalHostName();
    }

    public String toString() {
        return "Device{ip='" + this.ip + '\'' + ", hostname='" + this.hostname + '\'' + ", mac='" + this.mac + '\'' + ", time=" + this.time + '}';
    }
}
