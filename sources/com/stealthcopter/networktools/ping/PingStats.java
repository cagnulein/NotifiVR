package com.stealthcopter.networktools.ping;

import java.net.InetAddress;

public class PingStats {
    private final float averageTimeTaken;
    private final InetAddress ia;
    private final boolean isReachable;
    private final float maxTimeTaken;
    private final float minTimeTaken;
    private final long noPings;
    private final long packetsLost;

    public PingStats(InetAddress inetAddress, long j, long j2, float f, float f2, float f3) {
        this.ia = inetAddress;
        this.noPings = j;
        this.packetsLost = j2;
        this.averageTimeTaken = f / ((float) j);
        this.minTimeTaken = f2;
        this.maxTimeTaken = f3;
        this.isReachable = j - j2 > 0;
    }

    public InetAddress getAddress() {
        return this.ia;
    }

    public long getNoPings() {
        return this.noPings;
    }

    public long getPacketsLost() {
        return this.packetsLost;
    }

    public float getAverageTimeTaken() {
        return this.averageTimeTaken;
    }

    public float getMinTimeTaken() {
        return this.minTimeTaken;
    }

    public float getMaxTimeTaken() {
        return this.maxTimeTaken;
    }

    public boolean isReachable() {
        return this.isReachable;
    }

    public long getAverageTimeTakenMillis() {
        return (long) (this.averageTimeTaken * 1000.0f);
    }

    public long getMinTimeTakenMillis() {
        return (long) (this.minTimeTaken * 1000.0f);
    }

    public long getMaxTimeTakenMillis() {
        return (long) (this.maxTimeTaken * 1000.0f);
    }

    public String toString() {
        return "PingStats{ia=" + this.ia + ", noPings=" + this.noPings + ", packetsLost=" + this.packetsLost + ", averageTimeTaken=" + this.averageTimeTaken + ", minTimeTaken=" + this.minTimeTaken + ", maxTimeTaken=" + this.maxTimeTaken + '}';
    }
}
