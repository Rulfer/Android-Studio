package com.makingview.widgetexample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ExampleAppWidgetProvider extends AppWidgetProvider
{
    DateFormat df = new SimpleDateFormat("hh:mm:ss");
    AppCompatActivity app = new AppCompatActivity();
    DownloadClass dc;
    SaveAndLoad sl;
    RequestPermissions rp;

    private boolean downloading = false;

    private static final String MyOnClick = "launcher";
    private static final String MyOnClick1 = "updater";
    private static final Long MyOnDownloaded = null;

    boolean firstTime = true;

    Context masterContext;

    private DownloadManager downloadManager;
    Long queueID = null;
    final ArrayList<Long> ids = new ArrayList<Long>();

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        final int N = appWidgetIds.length;

        ComponentName thisWidget = new ComponentName(context, ExampleAppWidgetProvider.class);
        masterContext = context;

        // Perform this loop procedure for each App Widget that belongs to this
        // provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget1);
            views.setOnClickPendingIntent(R.id.button, getPendingSelfIntent(context, MyOnClick));
            views.setOnClickPendingIntent(R.id.update, getPendingSelfIntent1(context, MyOnClick1));

            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            context.getApplicationContext().registerReceiver(downloadReceiver, filter);
            //if(downloading)
            //    views.setLong(1, "waddup", dc.returnQueueId());


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

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            sl = new SaveAndLoad("asdasd");
            String temp = sl.Load();
            //Long otherTemp = Long.parseLong(temp);

            //Long test = ids.get(0);
            Toast asdf = Toast.makeText(context,
                "Queue ID is still: " + temp, Toast.LENGTH_LONG);
            asdf.setGravity(Gravity.TOP, 25, 400);
            asdf.show();



            if(queueID != null) {
                DownloadManager.Query DownloadQuery = new DownloadManager.Query();
                DownloadQuery.setFilterById(queueID);


                Cursor cursor = downloadManager.query(DownloadQuery);
                if (cursor.moveToFirst()) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1));
                    cursor.moveToFirst();
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    //This is String I pass to openFile method
                    String savedFilePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    //installDownloadedAPK(savedFilePath);
                    Toast toast = Toast.makeText(context,
                            "Download completed", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(context,
                            "Download Cancelled", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();
                }
            }
        }
    };

    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);//add this line

        if (MyOnClick.equals(intent.getAction()))
        {
            openApp(context);
        }
        else if(MyOnClick1.equals(intent.getAction()))
        {
            rp = new RequestPermissions();

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

        //dc = new DownloadClass(urlUri, context, filter);
        queueID = (DownloadData(urlUri, context));

        downloading = true;
    }

    private Long DownloadData (Uri uri, Context context) {

        long downloadReference;

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Downloading an update for something.");

        //Setting description of request
        request.setDescription("Android Data download using DownloadManager.");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "tempapk.apk"); //Saveposition of APK



        //Enqueue download and save the referenceId
        downloadReference = downloadManager.enqueue(request);

        ids.add(downloadReference);

        Toast asdf = Toast.makeText(context,
                "pls: " + ids.get(0), Toast.LENGTH_LONG);
        asdf.setGravity(Gravity.TOP, 25, 400);
        asdf.show();

        String pls = Long.toString(downloadReference);

        sl = new SaveAndLoad("oh shit");
        sl.Save(pls);

        /*View cancelButton = findViewById(R.id.cancel_download);
        cancelButton.setEnabled(true);
        cancelButton.setVisibility(View.VISIBLE);

        Button LayoutButton = (Button) findViewById(R.id.lay_below_me);
        LayoutButton.setVisibility(View.INVISIBLE);*/

        return downloadReference;
    }
}