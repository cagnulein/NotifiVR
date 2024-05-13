package com.stealthcopter.networktools;

import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.subnet.Device;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SubnetDevices {
    /* access modifiers changed from: private */
    public ArrayList<String> addresses;
    /* access modifiers changed from: private */
    public boolean cancelled = false;
    /* access modifiers changed from: private */
    public ArrayList<Device> devicesFound;
    /* access modifiers changed from: private */
    public HashMap<String, String> ipMacHashMap = null;
    private OnSubnetDeviceFound listener;
    /* access modifiers changed from: private */
    public int noThreads = 100;
    /* access modifiers changed from: private */
    public int timeOutMillis = 2500;

    public interface OnSubnetDeviceFound {
        void onDeviceFound(Device device);

        void onFinished(ArrayList<Device> arrayList);
    }

    private SubnetDevices() {
    }

    public static SubnetDevices fromLocalAddress() {
        InetAddress localIPv4Address = IPTools.getLocalIPv4Address();
        if (localIPv4Address != null) {
            return fromIPAddress(localIPv4Address.getHostAddress());
        }
        throw new IllegalAccessError("Could not access local ip address");
    }

    public static SubnetDevices fromIPAddress(InetAddress inetAddress) {
        return fromIPAddress(inetAddress.getHostAddress());
    }

    public static SubnetDevices fromIPAddress(String str) {
        if (IPTools.isIPv4Address(str)) {
            SubnetDevices subnetDevices = new SubnetDevices();
            subnetDevices.addresses = new ArrayList<>();
            subnetDevices.addresses.addAll(ARPInfo.getAllIPAddressesInARPCache());
            String substring = str.substring(0, str.lastIndexOf(".") + 1);
            for (int i = 0; i < 255; i++) {
                ArrayList<String> arrayList = subnetDevices.addresses;
                if (!arrayList.contains(substring + i)) {
                    ArrayList<String> arrayList2 = subnetDevices.addresses;
                    arrayList2.add(substring + i);
                }
            }
            return subnetDevices;
        }
        throw new IllegalArgumentException("Invalid IP Address");
    }

    public static SubnetDevices fromIPList(List<String> list) {
        SubnetDevices subnetDevices = new SubnetDevices();
        subnetDevices.addresses = new ArrayList<>();
        subnetDevices.addresses.addAll(list);
        return subnetDevices;
    }

    public SubnetDevices setNoThreads(int i) throws IllegalArgumentException {
        if (i >= 1) {
            this.noThreads = i;
            return this;
        }
        throw new IllegalArgumentException("Cannot have less than 1 thread");
    }

    public SubnetDevices setTimeOutMillis(int i) throws IllegalArgumentException {
        if (i >= 0) {
            this.timeOutMillis = i;
            return this;
        }
        throw new IllegalArgumentException("Timeout cannot be less than 0");
    }

    public void cancel() {
        this.cancelled = true;
    }

    public SubnetDevices findDevices(final OnSubnetDeviceFound onSubnetDeviceFound) {
        this.listener = onSubnetDeviceFound;
        this.cancelled = false;
        this.devicesFound = new ArrayList<>();
        new Thread(new Runnable() {
            public void run() {
                HashMap unused = SubnetDevices.this.ipMacHashMap = ARPInfo.getAllIPAndMACAddressesInARPCache();
                ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(SubnetDevices.this.noThreads);
                Iterator it = SubnetDevices.this.addresses.iterator();
                while (it.hasNext()) {
                    newFixedThreadPool.execute(new SubnetDeviceFinderRunnable((String) it.next()));
                }
                newFixedThreadPool.shutdown();
                try {
                    newFixedThreadPool.awaitTermination(1, TimeUnit.HOURS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HashMap unused2 = SubnetDevices.this.ipMacHashMap = ARPInfo.getAllIPAndMACAddressesInARPCache();
                Iterator it2 = SubnetDevices.this.devicesFound.iterator();
                while (it2.hasNext()) {
                    Device device = (Device) it2.next();
                    if (device.mac == null && SubnetDevices.this.ipMacHashMap.containsKey(device.ip)) {
                        device.mac = (String) SubnetDevices.this.ipMacHashMap.get(device.ip);
                    }
                }
                onSubnetDeviceFound.onFinished(SubnetDevices.this.devicesFound);
            }
        }).start();
        return this;
    }

    /* access modifiers changed from: private */
    public synchronized void subnetDeviceFound(Device device) {
        this.devicesFound.add(device);
        this.listener.onDeviceFound(device);
    }

    public class SubnetDeviceFinderRunnable implements Runnable {
        private final String address;

        SubnetDeviceFinderRunnable(String str) {
            this.address = str;
        }

        public void run() {
            if (!SubnetDevices.this.cancelled) {
                try {
                    InetAddress byName = InetAddress.getByName(this.address);
                    PingResult doPing = Ping.onAddress(byName).setTimeOutMillis(SubnetDevices.this.timeOutMillis).doPing();
                    if (doPing.isReachable) {
                        Device device = new Device(byName);
                        if (SubnetDevices.this.ipMacHashMap.containsKey(byName.getHostAddress())) {
                            device.mac = (String) SubnetDevices.this.ipMacHashMap.get(byName.getHostAddress());
                        }
                        device.time = doPing.timeTaken;
                        SubnetDevices.this.subnetDeviceFound(device);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
