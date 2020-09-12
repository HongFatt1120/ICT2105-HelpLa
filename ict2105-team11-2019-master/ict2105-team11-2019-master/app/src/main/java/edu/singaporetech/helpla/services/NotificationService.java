package edu.singaporetech.helpla.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import edu.singaporetech.helpla.IndexActivity;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.homeView.HomeFragment;
import edu.singaporetech.helpla.shake.ShakeFragment;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private static int securityIters = 8888888;  // for good luck, of course
    final static String CHANNEL_ID_NOTIFICAITON = "8888";
    final static String CHANNEL_ID_SECURITY = "8888";
    private final IBinder mBinder = new LocalBinder();
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public NotificationService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {

        public NotificationService getService() {
            return NotificationService.this;
        }
    }

    @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        SharedPreferences sharedPref = getSharedPreferences("userObj",MODE_PRIVATE);
                        if(sharedPref != null){
                            startNotification();
                        }

                    }
                }, 24, 24, TimeUnit.HOURS);
        return START_NOT_STICKY;
    }


    public void startNotification() {

        Log.d(TAG, "sendNotification()");
        sendNotification("HelpLa", "Wanna go Treasure Hunting", CHANNEL_ID_SECURITY, 9999);

    }


    public void sendNotification(String title, String content, String channel_ID, int id) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "name";
            String description = "this is descrpition";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channel_ID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


            Intent intent = new Intent(this, IndexActivity.class);
            intent.putExtra("From", "notifyFrag");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationManager.IMPORTANCE_MAX)
                    .setAutoCancel(false);
            NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(this);

            // notificationId is a unique int for each notification that you must define
            notificationManager1.notify(id, builder.build());
        }
    }


}
