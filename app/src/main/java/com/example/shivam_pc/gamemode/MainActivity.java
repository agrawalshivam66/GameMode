package com.example.shivam_pc.gamemode;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID ="GameModeChannel" ;
    private DecimalFormat Roundto4Decimalformat;
    NotificationManager mNotificationManager;
    TextView tvFreeRam_Memory;
    TextView tvText;
    ImageView ramInfoGraph;
    SharedPreferences SavedSettings;
    Button BoostButton;


    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.Roundto4Decimalformat = new DecimalFormat("00.00");
        this.tvFreeRam_Memory = (TextView) findViewById(R.id.tvFreeRAM_Memory);
        this.tvFreeRam_Memory.setText(this.Roundto4Decimalformat.format(getCurrentRAM_Memory()) + " GB");
        this.ramInfoGraph = (ImageView)findViewById(R.id.ramInfoGraph);
        this.BoostButton = (Button)findViewById(R.id.BoostButton);

        mNotificationManager  = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        SavedSettings = this.getSharedPreferences("SavedSettings",Context.MODE_PRIVATE);
        String DeviceModel = Build.MODEL;
        RefreshRamProgressBar();

        //Resume last boost status
        if(SavedSettings.getBoolean("BoostStatus",false)){
            BoostButton.setText("Stop Boosting");
        }
        else {
            BoostButton.setText("Boost");
        }

    }

    private void RefreshRamProgressBar() {
        int i = getPercentRAM_Memory();
        Log.e("tag", String.valueOf(i));
        circularImageBar(ramInfoGraph,100-i);
    }


    public void Boost(View view) {
        if(SavedSettings.getBoolean("BoostStatus",false)){
            stopBoost();//stop boost if true
     }
        else {
            startBoost();//boost if false
     }
    }

    //StartBoost function
    public void startBoost(){
        //DND only if All notification is true
        if(SavedSettings.getBoolean(getString(R.string.AllNotification),true)) {
            startDND();
        }

        //Clear Background only if ClearBackground is on
         if(SavedSettings.getBoolean("ClearBackground",true)){
            File appDir = new File(getCacheDir().getParent());
            MyApplication clrCache = new MyApplication();
            clrCache.clearApplicationData(appDir);
            MainActivity.this.clearMem();
            MainActivity.this.freeSpecificSpaceFromCache();
            MainActivity.this.killBackgroundApps();
            this.tvFreeRam_Memory.setText(this.Roundto4Decimalformat.format(getCurrentRAM_Memory()) + " GB");
            RefreshRamProgressBar();

        }
        //show notification
        createNotification();

        Toast.makeText(this, "Boosting Your game", Toast.LENGTH_SHORT).show();

        //change save settings boost status to true
        SavedSettings.edit().putBoolean("BoostStatus",true).apply();
        BoostButton.setText("Stop Boosting");
    }

    //Stop Boosting
    public void stopBoost(){
        Toast.makeText(this, "Stopping Boost", Toast.LENGTH_SHORT).show();
        SavedSettings.edit().putBoolean("BoostStatus",false).apply();
        stopDND();
        BoostButton.setText("Boost");
    }

    private void circularImageBar(ImageView iv2, int i) {
        Bitmap b = Bitmap.createBitmap(400, 400,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();

        paint.setColor(Color.parseColor("#c4c4c4"));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(150, 150, 140, paint);
        paint.setColor(Color.parseColor("#FF0000"));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL);
        final RectF oval = new RectF();
        paint.setStyle(Paint.Style.STROKE);
        oval.set(10,10,290,290);
        canvas.drawArc(oval, 180, ((i*360)/100), false, paint);
        paint.setStrokeWidth(0);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.parseColor("#8E8E93"));
        paint.setTextSize(100);
        canvas.drawText(""+i+"%", 150, 150+(paint.getTextSize()/3), paint);
        iv2.setImageBitmap(b);
    }


    public void startDND() {
        if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
            // Permission is not granted
            Toast.makeText(this, "Please allow Game Mode do not disturb access", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
        }
        else {
            Toast.makeText(this, "Starting Game Mode", Toast.LENGTH_SHORT).show();
            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
        }
    }

    public void stopDND() {
        if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
            // Permission is not granted
            Toast.makeText(this, "Please allow Game Mode do not disturb access", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
        } else {
            Toast.makeText(this, "Stopping Game Mode", Toast.LENGTH_SHORT);
            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        }
    }

    private void createNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent StopBoostIntent = new Intent(this,GameModeBroadcast.class);
        PendingIntent StopBoostPendingIntent =
                PendingIntent.getBroadcast(this, 0, StopBoostIntent, 0);

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //Boost Notification Builder
        NotificationCompat.Builder NotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.gameiconred)
                .setContentTitle("Game Mode ON ")
                .setContentText("BOOSTING YOUR GAME")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction( R.drawable.gameiconred,"Stop Boosting",
                        StopBoostPendingIntent);

        Notification notification = NotificationBuilder.build();
        //Showing Notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, NotificationBuilder.build());

    }

    private void killBackgroundApps() {
        List<ApplicationInfo> packages = getPackageManager().getInstalledApplications(0);
        ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        String myPackage = getApplicationContext().getPackageName();
        for (ApplicationInfo packageInfo : packages) {
            if (!((packageInfo.flags & 1) == 1 || packageInfo.packageName.equals(myPackage))) {
                mActivityManager.killBackgroundProcesses(packageInfo.packageName);

            }
        }
    }

    private void freeSpecificSpaceFromCache() {
        PackageManager pm = getPackageManager();
        for (Method m : pm.getClass().getDeclaredMethods()) {
            if (m.getName().equals("freeStorageAndNotify")) {
                try {
                    m.invoke(pm, new Object[]{Long.valueOf(Long.MAX_VALUE), null});
                    return;
                } catch (Exception e) {
                    Log.e("Exception cache", e + "");
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public void clearMem() {
        ActivityManager amgr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list = amgr.getRunningAppProcesses();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                RunningAppProcessInfo apinfo = (RunningAppProcessInfo) list.get(i);
                String[] pkgList = apinfo.pkgList;
                if (!apinfo.processName.startsWith("com.sec") && (apinfo.importance > 150 || apinfo.processName.contains("google"))) {
                    try {
                        Process.killProcess(apinfo.pid);
                        for (String killBackgroundProcesses : pkgList) {
                            amgr.killBackgroundProcesses(killBackgroundProcesses);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private double getCurrentRAM_Memory() {
        MemoryInfo mi = new MemoryInfo();
        ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getMemoryInfo(mi);
        double percentAvail = (double) (mi.availMem / mi.totalMem);
        return ((double) (mi.availMem / 1048576)) / 1024.0d;
    }
    private int getPercentRAM_Memory() {
        MemoryInfo mi = new MemoryInfo();
        ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getMemoryInfo(mi);
        Log.e("tag", "available memory: "+String.valueOf(mi.availMem));
        Log.e("tag", "total memory: "+String.valueOf(mi.totalMem));

        double longPercentAvail = ((double) mi.availMem / mi.totalMem) * 100;


        int percentAvail = (int)longPercentAvail;
        Log.e("tag", "Percent"+String.valueOf(percentAvail));
        return percentAvail;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.tvFreeRam_Memory.setText( this.Roundto4Decimalformat.format(getCurrentRAM_Memory()) + " GB");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.tvFreeRam_Memory.setText(this.Roundto4Decimalformat.format(getCurrentRAM_Memory()) + " GB");
    }

    public void settingsActivity(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);

    }

}

