package com.stealthcopter.networktools.ping;

public class PingOptions {
    private int timeToLive = 128;
    private int timeoutMillis = 1000;

    public int getTimeoutMillis() {
        return this.timeoutMillis;
    }

    public void setTimeoutMillis(int i) {
        this.timeoutMillis = Math.max(i, 1000);
    }

    public int getTimeToLive() {
        return this.timeToLive;
    }

    public void setTimeToLive(int i) {
        this.timeToLive = Math.max(i, 1);
    }
}
