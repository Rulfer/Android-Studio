package makingview.com.apkdownloader;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by rosse on 01/11/2016.
 */

public class StartMyServiceAtBootReceiver
{
    /*@Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceIntent = new Intent("com.myapp.NotifyService");
            context.startService(serviceIntent);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSmallIcon(R.drawable.ohyea);
            mBuilder.setContentTitle("My notification");
            mBuilder.setContentText("Hello World!");
            int mNotificationId = 001;
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
    }*/
}
