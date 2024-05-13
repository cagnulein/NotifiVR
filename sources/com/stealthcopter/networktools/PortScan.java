package com.stealthcopter.networktools;

import com.stealthcopter.networktools.portscanning.PortScanTCP;
import com.stealthcopter.networktools.portscanning.PortScanUDP;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PortScan {
    private static final int DEFAULT_THREADS_LOCALHOST = 7;
    private static final int DEFAULT_THREADS_LOCALNETWORK = 50;
    private static final int DEFAULT_THREADS_REMOTE = 50;
    private static final int METHOD_TCP = 0;
    private static final int METHOD_UDP = 1;
    private static final int TIMEOUT_LOCALHOST = 25;
    private static final int TIMEOUT_LOCALNETWORK = 1000;
    private static final int TIMEOUT_REMOTE = 2500;
    /* access modifiers changed from: private */
    public InetAddress address;
    /* access modifiers changed from: private */
    public boolean cancelled = false;
    /* access modifiers changed from: private */
    public int method = 0;
    /* access modifiers changed from: private */
    public int noThreads = 50;
    /* access modifiers changed from: private */
    public ArrayList<Integer> openPortsFound = new ArrayList<>();
    private PortListener portListener;
    /* access modifiers changed from: private */
    public ArrayList<Integer> ports = new ArrayList<>();
    /* access modifiers changed from: private */
    public int timeOutMillis = 1000;

    public interface PortListener {
        void onFinished(ArrayList<Integer> arrayList);

        void onResult(int i, boolean z);
    }

    private PortScan() {
    }

    public static PortScan onAddress(String str) throws UnknownHostException {
        return onAddress(InetAddress.getByName(str));
    }

    public static PortScan onAddress(InetAddress inetAddress) {
        PortScan portScan = new PortScan();
        portScan.setAddress(inetAddress);
        portScan.setDefaultThreadsAndTimeouts();
        return portScan;
    }

    public PortScan setTimeOutMillis(int i) {
        if (i >= 0) {
            this.timeOutMillis = i;
            return this;
        }
        throw new IllegalArgumentException("Timeout cannot be less than 0");
    }

    public PortScan setPort(int i) {
        this.ports.clear();
        validatePort(i);
        this.ports.add(Integer.valueOf(i));
        return this;
    }

    public PortScan setPorts(ArrayList<Integer> arrayList) {
        Iterator<Integer> it = arrayList.iterator();
        while (it.hasNext()) {
            validatePort(it.next().intValue());
        }
        this.ports = arrayList;
        return this;
    }

    public PortScan setPorts(String str) {
        this.ports.clear();
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (str != null) {
            for (String str2 : str.substring(str.indexOf(":") + 1, str.length()).split(",")) {
                if (str2.contains("-")) {
                    int parseInt = Integer.parseInt(str2.split("-")[0]);
                    int parseInt2 = Integer.parseInt(str2.split("-")[1]);
                    validatePort(parseInt);
                    validatePort(parseInt2);
                    if (parseInt2 > parseInt) {
                        while (parseInt <= parseInt2) {
                            arrayList.add(Integer.valueOf(parseInt));
                            parseInt++;
                        }
                    } else {
                        throw new IllegalArgumentException("Start port cannot be greater than or equal to the end port");
                    }
                } else {
                    int parseInt3 = Integer.parseInt(str2);
                    validatePort(parseInt3);
                    arrayList.add(Integer.valueOf(parseInt3));
                }
            }
            this.ports = arrayList;
            return this;
        }
        throw new IllegalArgumentException("Empty port string not allowed");
    }

    private void validatePort(int i) {
        if (i < 1) {
            throw new IllegalArgumentException("Start port cannot be less than 1");
        } else if (i > 65535) {
            throw new IllegalArgumentException("Start cannot be greater than 65535");
        }
    }

    public PortScan setPortsPrivileged() {
        this.ports.clear();
        for (int i = 1; i < 1024; i++) {
            this.ports.add(Integer.valueOf(i));
        }
        return this;
    }

    public PortScan setPortsAll() {
        this.ports.clear();
        for (int i = 1; i < 65536; i++) {
            this.ports.add(Integer.valueOf(i));
        }
        return this;
    }

    private void setAddress(InetAddress inetAddress) {
        this.address = inetAddress;
    }

    private void setDefaultThreadsAndTimeouts() {
        if (IPTools.isIpAddressLocalhost(this.address)) {
            this.timeOutMillis = 25;
            this.noThreads = 7;
        } else if (IPTools.isIpAddressLocalNetwork(this.address)) {
            this.timeOutMillis = 1000;
            this.noThreads = 50;
        } else {
            this.timeOutMillis = TIMEOUT_REMOTE;
            this.noThreads = 50;
        }
    }

    public PortScan setNoThreads(int i) throws IllegalArgumentException {
        if (i >= 1) {
            this.noThreads = i;
            return this;
        }
        throw new IllegalArgumentException("Cannot have less than 1 thread");
    }

    private PortScan setMethod(int i) {
        if (i == 0 || i == 1) {
            this.method = i;
            return this;
        }
        throw new IllegalArgumentException("Invalid method type " + i);
    }

    public PortScan setMethodUDP() {
        setMethod(1);
        return this;
    }

    public PortScan setMethodTCP() {
        setMethod(0);
        return this;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public ArrayList<Integer> doScan() {
        this.cancelled = false;
        this.openPortsFound.clear();
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(this.noThreads);
        Iterator<Integer> it = this.ports.iterator();
        while (it.hasNext()) {
            newFixedThreadPool.execute(new PortScanRunnable(this.address, it.next().intValue(), this.timeOutMillis, this.method));
        }
        newFixedThreadPool.shutdown();
        try {
            newFixedThreadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Collections.sort(this.openPortsFound);
        return this.openPortsFound;
    }

    public PortScan doScan(final PortListener portListener2) {
        this.portListener = portListener2;
        this.openPortsFound.clear();
        this.cancelled = false;
        new Thread(new Runnable() {
            public void run() {
                ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(PortScan.this.noThreads);
                Iterator it = PortScan.this.ports.iterator();
                while (it.hasNext()) {
                    int intValue = ((Integer) it.next()).intValue();
                    PortScan portScan = PortScan.this;
                    newFixedThreadPool.execute(new PortScanRunnable(portScan.address, intValue, PortScan.this.timeOutMillis, PortScan.this.method));
                }
                newFixedThreadPool.shutdown();
                try {
                    newFixedThreadPool.awaitTermination(1, TimeUnit.HOURS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (portListener2 != null) {
                    Collections.sort(PortScan.this.openPortsFound);
                    portListener2.onFinished(PortScan.this.openPortsFound);
                }
            }
        }).start();
        return this;
    }

    /* access modifiers changed from: private */
    public synchronized void portScanned(int i, boolean z) {
        if (z) {
            this.openPortsFound.add(Integer.valueOf(i));
        }
        if (this.portListener != null) {
            this.portListener.onResult(i, z);
        }
    }

    private class PortScanRunnable implements Runnable {
        private final InetAddress address;
        private final int method;
        private final int portNo;
        private final int timeOutMillis;

        PortScanRunnable(InetAddress inetAddress, int i, int i2, int i3) {
            this.address = inetAddress;
            this.portNo = i;
            this.timeOutMillis = i2;
            this.method = i3;
        }

        public void run() {
            if (!PortScan.this.cancelled) {
                int i = this.method;
                if (i == 0) {
                    PortScan portScan = PortScan.this;
                    int i2 = this.portNo;
                    portScan.portScanned(i2, PortScanTCP.scanAddress(this.address, i2, this.timeOutMillis));
                } else if (i == 1) {
                    PortScan portScan2 = PortScan.this;
                    int i3 = this.portNo;
                    portScan2.portScanned(i3, PortScanUDP.scanAddress(this.address, i3, this.timeOutMillis));
                } else {
                    throw new IllegalArgumentException("Invalid method");
                }
            }
        }
    }
}
