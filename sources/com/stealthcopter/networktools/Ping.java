package com.stealthcopter.networktools;

import com.stealthcopter.networktools.ping.PingOptions;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;
import com.stealthcopter.networktools.ping.PingTools;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Ping {
    public static final int PING_HYBRID = 2;
    public static final int PING_JAVA = 0;
    public static final int PING_NATIVE = 1;
    /* access modifiers changed from: private */
    public InetAddress address;
    private String addressString = null;
    /* access modifiers changed from: private */
    public boolean cancelled = false;
    /* access modifiers changed from: private */
    public int delayBetweenScansMillis = 0;
    /* access modifiers changed from: private */
    public final PingOptions pingOptions = new PingOptions();
    /* access modifiers changed from: private */
    public int times = 1;

    public interface PingListener {
        void onError(Exception exc);

        void onFinished(PingStats pingStats);

        void onResult(PingResult pingResult);
    }

    private Ping() {
    }

    public static Ping onAddress(String str) {
        Ping ping = new Ping();
        ping.setAddressString(str);
        return ping;
    }

    public static Ping onAddress(InetAddress inetAddress) {
        Ping ping = new Ping();
        ping.setAddress(inetAddress);
        return ping;
    }

    public Ping setTimeOutMillis(int i) {
        if (i >= 0) {
            this.pingOptions.setTimeoutMillis(i);
            return this;
        }
        throw new IllegalArgumentException("Times cannot be less than 0");
    }

    public Ping setDelayMillis(int i) {
        if (i >= 0) {
            this.delayBetweenScansMillis = i;
            return this;
        }
        throw new IllegalArgumentException("Delay cannot be less than 0");
    }

    public Ping setTimeToLive(int i) {
        if (i >= 1) {
            this.pingOptions.setTimeToLive(i);
            return this;
        }
        throw new IllegalArgumentException("TTL cannot be less than 1");
    }

    public Ping setTimes(int i) {
        if (i >= 0) {
            this.times = i;
            return this;
        }
        throw new IllegalArgumentException("Times cannot be less than 0");
    }

    private void setAddress(InetAddress inetAddress) {
        this.address = inetAddress;
    }

    private void setAddressString(String str) {
        this.addressString = str;
    }

    /* access modifiers changed from: private */
    public void resolveAddressString() throws UnknownHostException {
        String str;
        if (this.address == null && (str = this.addressString) != null) {
            this.address = InetAddress.getByName(str);
        }
    }

    public void cancel() {
        this.cancelled = true;
    }

    public PingResult doPing() throws UnknownHostException {
        this.cancelled = false;
        resolveAddressString();
        return PingTools.doPing(this.address, this.pingOptions);
    }

    public Ping doPing(final PingListener pingListener) {
        new Thread(new Runnable() {
            public void run() {
                float f;
                float f2;
                float f3;
                long j;
                long j2;
                try {
                    Ping.this.resolveAddressString();
                    if (Ping.this.address == null) {
                        pingListener.onError(new NullPointerException("Address is null"));
                        return;
                    }
                    float f4 = 0.0f;
                    boolean unused = Ping.this.cancelled = false;
                    int access$300 = Ping.this.times;
                    long j3 = 0;
                    long j4 = 0;
                    float f5 = -1.0f;
                    float f6 = -1.0f;
                    while (true) {
                        if (access$300 <= 0 && Ping.this.times != 0) {
                            f3 = f4;
                            j2 = j3;
                            f = f5;
                            j = j4;
                            f2 = f6;
                            break;
                        }
                        PingResult doPing = PingTools.doPing(Ping.this.address, Ping.this.pingOptions);
                        PingListener pingListener = pingListener;
                        if (pingListener != null) {
                            pingListener.onResult(doPing);
                        }
                        j3++;
                        if (doPing.hasError()) {
                            j4++;
                        } else {
                            float timeTaken = doPing.getTimeTaken();
                            f4 += timeTaken;
                            if (f5 == -1.0f || timeTaken > f5) {
                                f5 = timeTaken;
                            }
                            if (f6 == -1.0f || timeTaken < f6) {
                                f6 = timeTaken;
                            }
                        }
                        long j5 = j4;
                        float f7 = f5;
                        float f8 = f4;
                        access$300--;
                        if (Ping.this.cancelled) {
                            j2 = j3;
                            f3 = f8;
                            f = f7;
                            f2 = f6;
                            j = j5;
                            break;
                        }
                        try {
                            Thread.sleep((long) Ping.this.delayBetweenScansMillis);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        f4 = f8;
                        f5 = f7;
                        j4 = j5;
                    }
                    PingListener pingListener2 = pingListener;
                    if (pingListener2 != null) {
                        pingListener2.onFinished(new PingStats(Ping.this.address, j2, j, f3, f2, f));
                    }
                } catch (UnknownHostException e2) {
                    pingListener.onError(e2);
                }
            }
        }).start();
        return this;
    }
}
