package com.makingview.mvlauncher;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.GregorianCalendar;

/**
 * Created by Bård on 31.10.2016.
 */

public class AlarmReceiver extends BroadcastReceiver
{
    ReadXmlFile rxf;
    CheckAppVersion cav;

    private int xmlMovieMenuVersion = 0;
    private int xmlLauncherVersion = 0;

    private boolean updateMovieMenu = false;
    private boolean updateLauncher = false;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.uuuuuwat);
        mBuilder.setContentTitle("My notification");
        mBuilder.setContentText("Hello World!");
        int mNotificationId = 001;

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        Toast.makeText(context, "beep beep!", Toast.LENGTH_SHORT).show();

        rxf = new ReadXmlFile();
        rxf.fetchXML();

        while(rxf.parsingComplete && rxf.downloadFailed == false)
        {
            xmlMovieMenuVersion = rxf.returnMovieMenuVersion();
            xmlLauncherVersion = rxf.returnLauncherVersion();
        }

        cav = new CheckAppVersion();
        cav.checkAllApps(context, xmlMovieMenuVersion, xmlLauncherVersion);


        updateMovieMenu = cav.returnMovieMenu();
        updateLauncher = cav.returnLauncher();

        Log.d("pls update domsehint", "now");
        if(updateMovieMenu == true)
        {
            Log.d("update", "MovieMenu");
            sendMessage(context, "update movieMenu");
        }
        if(updateLauncher == true)
        {
            Log.d("update", "launcher");
            sendMessage(context, "update launcher");
        }

        rxf.reset();
        cav.reset();

        initiateAlarm(context);
    }

    private void sendMessage(Context context, String message)
    {
        Log.d("Update an app", message);
        Intent intent = new Intent("custom-event-name");
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    void initiateAlarm(Context context)
    {
        // time at which alarm will be scheduled here alarm is scheduled at 1 day from current time,
        // we fetch  the current time in milliseconds and added 1 day time
        // i.e. 24*60*60*1000= 86,400,000   milliseconds in a day

        //Long time = new GregorianCalendar().getTimeInMillis()+24*60*60*1000;
        Long time = new GregorianCalendar().getTimeInMillis()+ 10000;
        // create an Intent and set the class which will execute when Alarm triggers, here we have
        // given AlarmReciever in the Intent, the onRecieve() method of this class will execute when
        // alarm triggers and
        //we call the method inside onRecieve() method pf Alarmreciever class
        Intent intentAlarm = new Intent(context, AlarmReceiver.class);

        // create the object
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(context,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(context, "Alarm Scheduled by AlarmReciever", Toast.LENGTH_SHORT).show();
    }
}
