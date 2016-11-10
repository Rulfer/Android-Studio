package com.makingview.mvlauncher;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Bård on 08.11.2016.
 */

public class HomeActivity extends Activity
{
    CheckAppVersion cav;
    AlarmReceiver ar;

    private String downloadPath = "http://content.makingview.com/LauncherFiles/MovieMenu.apk";
    private String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
    private DownloadManager downloadManager;
    public Long tempID;
    public String pls;
    public List<Long> queueID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

        LocalBroadcastManager.getInstance(this).registerReceiver(localMessageReciever,
                new IntentFilter("custom-event-name"));

        cav = new CheckAppVersion();
        cav.checkAllApps(HomeActivity.this);

        initiateAlarm();
    }

    public void openMovieMenu(View view) {
        PackageManager manager = getPackageManager();

        Intent i = manager.getLaunchIntentForPackage("com.MakingView.movieMenu");

        if (i != null) {
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        }
    }

    public void downloadMovieMenu()
    {
        Uri tempUri = Uri.parse(downloadPath);
        tempID = DownloadData(tempUri);
        queueID.add(tempID);
    }

    public void installDownloadedAPK(String fileName)
    {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl((fileName));
        String type = map.getMimeTypeFromExtension(ext);

        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(new File(fileName)), type);
        startActivity(install);
    }

    //Function that initiates the Android Download Manager class.
    //This class allows us to download files and display the download queue, without having to
    //create the functionality ourself.
    public long DownloadData (Uri uri) {

        long downloadReference; //Reference to the object to be downloaded

        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE); //A reference to the download class
        DownloadManager.Request request = new DownloadManager.Request(uri); //The file to download

        //Setting title of request
        request.setTitle("Downloading " + "MovieMenu.apk"); //Title of the download to be displayed on the phone

        //Setting description of request
        request.setDescription("Movie Menu update"); //Description of the download

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(HomeActivity.this, Environment.DIRECTORY_DOWNLOADS, "MovieMenu.apk"); //Saveposition of APK

        //Enqueue download and save the referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference; //Return the download refence, so that the code easily can access the downloaded file
    }

    //This reciever is used by AlarmReciever.java to tell this class that it should download the XML document
    //and check for updated files.
    private BroadcastReceiver localMessageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");

            if(message == "initiate download")
            {
                downloadMovieMenu();
            }
        }
    };

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("pls", "asdfg: " + pls);
            //Log.d("queueID", queueID.get(0).toString());
            try {
                DownloadManager.Query DownloadQuery = new DownloadManager.Query();
                DownloadQuery.setFilterById(queueID.get(0));

                Cursor cursor = downloadManager.query(DownloadQuery);

                if (cursor.moveToFirst()) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1));
                    cursor.moveToFirst();
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    //This is String I pass to openFile method
                    String savedFilePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));

                    installDownloadedAPK(savedFilePath);

                    Toast toast = Toast.makeText(HomeActivity.this,
                            "Download Completed", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(HomeActivity.this,
                            "Download Cancelled", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();
                }
            }
            catch (Exception e)
            {
                Log.d("Download failed", e.toString());
            }

            try{
            queueID.remove(0);
            }
            catch (Exception e)
            {
                Log.d("queueID is empty", e.toString());
            }
        }
    };

    public void initiateAlarm()
    {
        // time at which alarm will be scheduled here alarm is scheduled at 1 day from current time,
        // we fetch  the current time in milliseconds and added 1 day time
        // i.e. 24*60*60*1000= 86,400,000   milliseconds in a day

        //Long time = new GregorianCalendar().getTimeInMillis()+24*60*60*1000;
        Long time = new GregorianCalendar().getTimeInMillis()+ 5000;
        // create an Intent and set the class which will execute when Alarm triggers, here we have
        // given AlarmReciever in the Intent, the onRecieve() method of this class will execute when
        // alarm triggers and
        //we call the method inside onRecieve() method pf Alarmreciever class
        Intent intentAlarm = new Intent(HomeActivity.this, AlarmReceiver.class);

        // create the object
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(this, "Alarm Scheduled for Tommrrow", Toast.LENGTH_LONG).show();
    }
}
