package com.example.vrphone;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    public static String appName;
    public static Context cont;
    public static View iView;
    public static View iView2;
    public static Intent intent;
    /* access modifiers changed from: private */
    public static PrintWriter mBufferOut;
    public static View pBar;
    public static Socket socket;
    public BroadcastReceiver MyReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i("MainActivity", "Broadcast Recieved: " + intent.getStringExtra("serviceMessage"));
            intent.getStringExtra("serviceMessage");
        }
    };

    /* access modifiers changed from: package-private */
    public void lol() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        intent = new Intent(getApplicationContext(), NLService.class);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(-2, -2, 8388629);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setIcon((int) R.drawable.inv);
        supportActionBar.setTitle((CharSequence) Html.fromHtml("<font color='#ebebeb'>" + getResources().getString(R.string.app_name) + " </font>"));
        final ImageView imageView = new ImageView(this);
        imageView.setImageIcon(Icon.createWithResource(this, R.drawable.inv));
        supportActionBar.setCustomView(imageView, layoutParams);
        supportActionBar.setDisplayShowCustomEnabled(true);
        final SharedPreferences sharedPreferences = getSharedPreferences("Questnotifier", 0);
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        if (!sharedPreferences.contains("running")) {
            edit.putBoolean("running", false);
            edit.commit();
        }
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main);
        cont = this;

        iView = findViewById(R.id.iView);
        iView2 = findViewById(R.id.iView2);

        Intent serviceIntent = new Intent(this, DeviceFinderService.class);
        startService(serviceIntent);

        if (sharedPreferences.getBoolean("running", false)) {
            Log.d("StaiyQ", "1");
            Log.d("StaiyQ", Boolean.toString(sharedPreferences.getBoolean("running", false)));
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Log.d("StaiyQ", "2");
                        Socket socket = new Socket();
                        socket.setSoTimeout(100);
                        socket.connect(new InetSocketAddress(sharedPreferences.getString("devip", BuildConfig.FLAVOR), 11000), 100);
                        Log.d("StaiyQ", Boolean.toString(socket.isConnected()));
                        if (!socket.isConnected()) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Log.d("StaiyQ", "3");
                                    edit.putBoolean("running", false);
                                    edit.commit();
                                }
                            });
                            return;
                        }
                        PrintWriter unused = MainActivity.mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), false);
                        MainActivity.mBufferOut.println("ping<EOF>");
                        MainActivity.mBufferOut.flush();
                        MainActivity.mBufferOut.close();
                        socket.close();
                    } catch (Exception e) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                edit.putBoolean("running", false);
                                edit.commit();
                            }
                        });
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        iView2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    MainActivity.this.stopService(MainActivity.intent);
                } catch (Exception unused) {
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        edit.putBoolean("running", false);
                        edit.commit();
                    }
                });
            }
        });
        iView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
                    public void onDeviceFound(Device device) {
                        String str = device.ip;
                    }

                    public void onFinished(ArrayList<Device> arrayList) {
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
                                        SharedPreferences.Editor edit = MainActivity.this.getSharedPreferences("Questnotifier", 0).edit();
                                        edit.putBoolean("running", true);
                                        edit.commit();
                                        edit.putString("devip", next.ip);
                                        edit.commit();
                                        MainActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                MainActivity.iView.setVisibility(4);
                                                MainActivity.pBar.setVisibility(4);
                                                MainActivity.iView2.setVisibility(0);
                                                Toast.makeText(MainActivity.this, "Connected!", 1).show();
                                            }
                                        });
                                        return;
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
                        if (!sharedPreferences.getBoolean("running", false)) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    SharedPreferences.Editor edit = MainActivity.this.getSharedPreferences("Questnotifier", 0).edit();
                                    edit.putBoolean("running", false);
                                    edit.commit();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle((CharSequence) "Failure");
                                    builder.setMessage((CharSequence) "No compatible device found");
                                    builder.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) null);
                                    builder.show();
                                }
                            });
                        }
                    }
                });
            }
        });
        appName = getResources().getString(R.string.app_name);
    }

    public Boolean IsPermisionGranted() {
        ComponentName componentName = new ComponentName(this, NLService.class);
        String string = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        return Boolean.valueOf(string != null && string.contains(componentName.flattenToString()));
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (!IsPermisionGranted().booleanValue()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage((CharSequence) appName + " needs permission to read notifications. Allow it in the following menu");
            builder.setTitle((CharSequence) "Permission needed");
            builder.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.this.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            });
            builder.setCancelable(false);
            builder.create().show();
        }
    }

    @NonNull
    public static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return createBitmap;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(this.MyReceiver, new IntentFilter(NLService.ACTION_STATUS_BROADCAST));
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.MyReceiver);
    }
}
