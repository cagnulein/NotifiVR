package com.example.vrphone;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = 18)
public class NLService extends NotificationListenerService {
    public static final String ACTION_STATUS_BROADCAST = "com.example.notifyservice.NLService_Status";
    static List<String> curPosted = new ArrayList();
    private String TAG = getClass().getSimpleName();
    private int nAdded = 0;
    private int nRemoved = 0;
    private NLServiceReceiver nlservicereciver;
    int temp = 5;

    public int onStartCommand(Intent intent, int i, int i2) {
        super.onStartCommand(intent, i, i2);
        return 1;
    }

    @RequiresApi(api = 24)
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        NotificationListenerService.requestRebind(ComponentName.createRelative(getPackageName(), NLService.class.getCanonicalName()));
        return true;
    }

    private void broadcastStatus() {
        Log.i("NLService", "Broadcasting status added(" + this.nAdded + ")/removed(" + this.nRemoved + ")");
        Intent intent = new Intent(ACTION_STATUS_BROADCAST);
        intent.putExtra("serviceMessage", "Added: " + this.nAdded + " | Removed: " + this.nRemoved);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences("Questnotifier", 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        if (!sharedPreferences.contains("running")) {
            edit.putBoolean("running", false);
            edit.commit();
        }
        new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Questnotifier").setContentText("Running...").build();
        Log.i("NLService", "NLService created!");
        this.nlservicereciver = new NLServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.notifyservice.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(this.nlservicereciver, intentFilter);
        Log.i("NLService", "NLService created!");
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.nlservicereciver);
        Log.i("NLService", "NLService destroyed!");
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        Notification notification = statusBarNotification.getNotification();
        SharedPreferences sharedPreferences = getSharedPreferences("Questnotifier", 0);
        boolean z = sharedPreferences.getBoolean("running", false);
        final String string = sharedPreferences.getString("devip", BuildConfig.FLAVOR);
        if (z) {
            try {
                if (!sharedPreferences.getBoolean("filter", false) || sharedPreferences.getString("filterapps", BuildConfig.FLAVOR).contains(statusBarNotification.getPackageName())) {
                    String charSequence = notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE).toString();
                    String charSequence2 = notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT).toString();
                    if (charSequence == null) {
                        charSequence = "NoTitle";
                    }
                    if (charSequence2 == null) {
                        charSequence2 = "NoText";
                    }
                    if (charSequence.length() <= 0) {
                        charSequence = "NoTitle";
                    }
                    if (charSequence2.length() <= 0) {
                        charSequence2 = "NoText";
                    }
                    Icon largeIcon = statusBarNotification.getNotification().getLargeIcon();
                    if (largeIcon == null) {
                        largeIcon = statusBarNotification.getNotification().getSmallIcon();
                    }
                    if (largeIcon == null) {
                        largeIcon = Icon.createWithResource(this, R.drawable.circle);
                    }
                    if (!curPosted.contains(charSequence + charSequence2)) {
                        curPosted.add(charSequence + charSequence2);
                        Bundle bundle = notification.extras;
                        Drawable drawable = null;
                        if (largeIcon == null) {
                            try {
                                drawable = getPackageManager().getApplicationIcon(statusBarNotification.getPackageName());
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            drawable = largeIcon.loadDrawable(this);
                        }
                        Bitmap bitmapFromDrawable = MainActivity.getBitmapFromDrawable(drawable);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmapFromDrawable.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        final String str = charSequence + "|" + charSequence2 + "|" + Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0) + "<EOF>";
                        new Thread() {
                            public void run() {
                                try {
                                    Socket socket = new Socket(string, 11000);
                                    PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), false);
                                    printWriter.println(str);
                                    printWriter.flush();
                                    printWriter.close();
                                    socket.close();
                                } catch (IOException unused) {
                                }
                            }
                        }.start();
                    }
                }
            } catch (Exception unused) {
            }
        }
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        Log.i(this.TAG, "********** onNOtificationRemoved");
        Log.i(this.TAG, "ID :" + statusBarNotification.getId() + "t" + statusBarNotification.getNotification().tickerText + "\t" + statusBarNotification.getPackageName());
        Intent intent = new Intent("com.example.notify.NOTIFICATION_LISTENER_EXAMPLE");
        StringBuilder sb = new StringBuilder();
        sb.append("onNotificationRemoved :");
        sb.append(statusBarNotification.getPackageName());
        sb.append("\n");
        intent.putExtra("notification_event", sb.toString());
        sendBroadcast(intent);
        this.nRemoved++;
        broadcastStatus();
    }

    class NLServiceReceiver extends BroadcastReceiver {
        NLServiceReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("command").equals("list")) {
                Intent intent2 = new Intent("com.example.notify.NOTIFICATION_LISTENER_EXAMPLE");
                intent2.putExtra("notification_event", "=====================");
                NLService.this.sendBroadcast(intent2);
                int i = 1;
                for (StatusBarNotification statusBarNotification : NLService.this.getActiveNotifications()) {
                    Intent intent3 = new Intent("com.example.notify.NOTIFICATION_LISTENER_EXAMPLE");
                    intent3.putExtra("notification_event", i + " " + statusBarNotification.getPackageName() + "\n");
                    NLService.this.sendBroadcast(intent3);
                    i++;
                }
                Intent intent4 = new Intent("com.example.notify.NOTIFICATION_LISTENER_EXAMPLE");
                intent4.putExtra("notification_event", "===== Notification List ====");
                NLService.this.sendBroadcast(intent4);
            }
        }
    }
}
