package com.makingview.widgetexample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ExampleAppWidgetProvider extends AppWidgetProvider {
    DateFormat df = new SimpleDateFormat("hh:mm:ss");
    AppCompatActivity app = new AppCompatActivity();
    DownloadClass dc;
    Long queueID;
    private DownloadManager downloadManager;

    private static final String MyOnClick = "launcher";
    private static final String MyOnClick1 = "updater";


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        ComponentName thisWidget = new ComponentName(context, ExampleAppWidgetProvider.class);

        // Perform this loop procedure for each App Widget that belongs to this
        // provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget1);
            views.setOnClickPendingIntent(R.id.button, getPendingSelfIntent(context, MyOnClick));
            views.setOnClickPendingIntent(R.id.update, getPendingSelfIntent1(context, MyOnClick1));

            // To update a label
            //views.setTextViewText(R.id.widget1label, df.format(new Date()));

            // Tell the AppWidgetManager to perform an update on the current app
            // widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    protected PendingIntent getPendingSelfIntent1(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);//add this line

        if (MyOnClick.equals(intent.getAction()))
        {
            openApp(context);
        }
        else if(MyOnClick1.equals(intent.getAction()))
        {
            updateApp(context);
        }
    };

    public void openApp(Context context)
    {
        PackageManager manager = context.getPackageManager();

        Intent i = manager.getLaunchIntentForPackage("com.MakingView.movieMenu");

        if(i != null)
        {
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
        }
    }

    public void updateApp(Context context)
    {
        Uri urlUri = Uri.parse("https://content.makingview.com/apks/MovieMenu.apk");

        dc = new DownloadClass(urlUri, context);
    }


}