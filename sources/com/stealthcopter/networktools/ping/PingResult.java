package com.stealthcopter.networktools.ping;

import java.net.InetAddress;

public class PingResult {
    public String error = null;
    public String fullString;
    public final InetAddress ia;
    public boolean isReachable;
    public String result;
    public float timeTaken;

    public PingResult(InetAddress inetAddress) {
        this.ia = inetAddress;
    }

    public boolean isReachable() {
        return this.isReachable;
    }

    public boolean hasError() {
        return this.error != null;
    }

    public float getTimeTaken() {
        return this.timeTaken;
    }

    public String getError() {
        return this.error;
    }

    public InetAddress getAddress() {
        return this.ia;
    }

    public String toString() {
        return "PingResult{ia=" + this.ia + ", isReachable=" + this.isReachable + ", error='" + this.error + '\'' + ", timeTaken=" + this.timeTaken + ", fullString='" + this.fullString + '\'' + ", result='" + this.result + '\'' + '}';
    }
}
