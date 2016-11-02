package com.makingview.widgetexample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RemoteViews;

public class ExampleAppWidgetProvider extends AppWidgetProvider {
    DateFormat df = new SimpleDateFormat("hh:mm:ss");
    AppCompatActivity app = new AppCompatActivity();

    private static final String MyOnClick = "myOnClickTag";


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        ComponentName thisWidget = new ComponentName(context, ExampleAppWidgetProvider.class);

        // Perform this loop procedure for each App Widget that belongs to this
        // provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            Log.d("fuck me", "3");


            // Create an Intent to launch ExampleActivity
            //Intent intent = new Intent(context, ExampleAppWidgetProvider.class);

            //Intent intent = app.getPackageManager().getLaunchIntentForPackage("com.makingview.MovieMenu");

            //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget1);
            views.setOnClickPendingIntent(R.id.button, getPendingSelfIntent(context, MyOnClick));

            // To update a label
            views.setTextViewText(R.id.widget1label, df.format(new Date()));

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

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);//add this line
        Log.d("fuck me", "|");

        if (MyOnClick.equals(intent.getAction()))
        {
            Log.d("fuck me", "2");
            //your onClick action is here
            openApp(context);
        }
    };

    public static void openApp(Context context)
    {
        PackageManager manager = context.getPackageManager();
        Log.d("fuck me", "3");

        //Intent i = manager.getLaunchIntentForPackage("com.MakingView.movieMenu");
        Intent i = manager.getLaunchIntentForPackage("com.MakingView.singleMovieplayer");
        Log.d("fuck me", "4");

        if(i != null)
        {
            Log.d("fuck me", "5");

            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
        }
    }
}