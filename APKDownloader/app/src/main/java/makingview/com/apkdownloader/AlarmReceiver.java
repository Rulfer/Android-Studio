package makingview.com.apkdownloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

/**
 * Created by Bård on 31.10.2016.
 */

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ohyea);
        mBuilder.setContentTitle("My notification");
        mBuilder.setContentText("Hello World!");
        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());



        Toast.makeText(context, "beep beep!", Toast.LENGTH_LONG).show();
    }
}
