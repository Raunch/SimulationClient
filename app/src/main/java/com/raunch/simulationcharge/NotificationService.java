package com.raunch.simulationcharge;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by songshunzhang on 16-2-29.
 */
public class NotificationService extends Service {
    private NotificationManager mNM;

    @Override
    public void onCreate() {
        super.onCreate();
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //showNotification("Hello world");
        sendMyIntent();


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMyIntent() {
        Intent intent = new Intent();
        intent.setAction("com.njck.payment");
        intent.putExtra("index","002");
        sendBroadcast(intent);
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification(String text) {
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.gc_icon, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.service_start_arguments_label),
                text, contentIntent);

        // We show this for as long as our service is processing a command.
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.service_created, notification);
    }

    private void hideNotification() {
        mNM.cancel(R.string.service_created);
    }

    @Override
    public void onDestroy() {
        //hideNotification();
    }
}
