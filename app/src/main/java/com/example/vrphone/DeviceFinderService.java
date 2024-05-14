package com.example.vrphone;

import static java.lang.Thread.sleep;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;

import android.graphics.drawable.Icon;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;

public class DeviceFinderService extends Service {
    private boolean isRunning = false;
    private boolean isSearching = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRunning) {
                        if (!isSearching) {
                            isSearching = true;
                            SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
                                public void onDeviceFound(Device device) {
                                    String str = device.ip;
                                    // Resto del codice per gestire il dispositivo trovato
                                }

                                public void onFinished(ArrayList<Device> arrayList) {
                                    if (arrayList.isEmpty()) {
                                        // Nessun dispositivo trovato, riprova dopo un ritardo
                                        try {
                                            sleep(5000); // Ritardo di 5 secondi
                                            isSearching = false;
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        try {
                                            Iterator<Device> it = arrayList.iterator();
                                            while (it.hasNext()) {
                                                Device next = it.next();
                                                try {
                                                    Log.d("StaiyQ", "2");
                                                    Socket socket = new Socket();
                                                    socket.connect(new InetSocketAddress(next.ip, 11000), ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                                                    if (socket.isConnected()) {
                                                        MainActivity.cont.startService(MainActivity.intent);
                                                        Bitmap bitmapFromDrawable = MainActivity.getBitmapFromDrawable(Icon.createWithResource(MainActivity.cont, R.mipmap.ic_launcher).loadDrawable(MainActivity.cont));
                                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                        bitmapFromDrawable.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                                        String encodeToString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
                                                        PrintWriter unused = MainActivity.mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), false);
                                                        PrintWriter access$000 = MainActivity.mBufferOut;
                                                        access$000.println(MainActivity.appName + "| Connected|" + encodeToString + "<EOF>");
                                                        MainActivity.mBufferOut.flush();
                                                        MainActivity.mBufferOut.close();
                                                        socket.close();
                                                        // ... (resto del codice)
                                                    }
                                                    socket.close();
                                                } catch (IOException e) {
                                                    try {
                                                        Log.i("StaiyRes", e.getMessage());
                                                    } catch (Exception unused2) {
                                                    }
                                                }
                                            }
                                        } catch (Exception unused3) {
                                        }
                                    }
                                    isSearching = false;
                                }
                        });
                        } else {
                            try {
                                sleep(5000); // Ritardo di 5 secondi
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }).start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}