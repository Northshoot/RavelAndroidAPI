package ai.harmony.ravel.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import ai.harmony.ravel.R;
import ai.harmony.ravel.defines.RavelDefines;

/**
 * Created by lauril on 2/8/16.
 */
public class RavelNotificationCenter{
    public static RavelNotificationCenter INSTANCE = new RavelNotificationCenter();

    Context mContext;
    private NotificationManager mNM;
    private int NOTIFICATION = R.string.local_service_started;

    public void setContext(Context context){
        this.mContext = context;
        mNM = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private RavelNotificationCenter() {

    }


    public void showNotification(int msg, Class<? extends Activity> cls){
        if(mContext == null) return;
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = mContext.getText(msg);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, cls), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(mContext)
                .setSmallIcon(R.drawable.ic_connection_mgc_5)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(mContext.getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }


//    private void sendNotification(String message) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_connection_mc_4)
//                .setContentTitle("Ravel Controller")
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }

    public void cancel() {
        mNM.cancel(NOTIFICATION);
    }
    /**
     * Show a notification while this service is running.
     */
    public void showNotification() {
        if(mContext == null) return;
        showNotification(R.string.local_service_started, ai.harmony.app.MainActivity.class);
    }

    public void setConnectionIcon(int connection){
        if(mContext == null) return;
        int icon = R.drawable.ic_stat_mgc;
        switch (connection){
            case (RavelDefines.CONNECTION_C):
                icon = R.drawable.ic_stat_c;
                break;
            case (RavelDefines.CONNECTION_G):
                icon = R.drawable.ic_stat_g;
                break;
            case (RavelDefines.CONNECTION_GC):
                icon = R.drawable.ic_stat_gc;
                break;
            case (RavelDefines.CONNECTION_M):
                icon = R.drawable.ic_stat_m;
                break;
            case (RavelDefines.CONNECTION_MC):
                icon = R.drawable.ic_stat_mc;
                break;
            case (RavelDefines.CONNECTION_MG):
                icon = R.drawable.ic_stat_mg;
                break;
            case (RavelDefines.CONNECTION_MGC):
                icon = R.drawable.ic_stat_mgc;
                break;
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(icon)
                        .setContentTitle("Ravel");
        mNM.notify(NOTIFICATION,mBuilder.build());

    }
}
