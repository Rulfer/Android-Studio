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
import android.widget.Button;
import android.widget.ImageButton;
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
    RequestPermissions rp;
    SaveAndLoad sal;

    private String movieMenuApkPath = "http://video.makingview.no/apps/gearVR/apks/MovieMenu.apk";
    private String launcherApkPath = "http://video.makingview.no/apps/gearVR/apks/app-release.apk";

    private String type = "application/vnd.android.package-archive";
    private String launcherPath;
    private String movieMenuPath;

    private DownloadManager downloadManager;

    public List<Long> queueID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkIfUpdateButtonsShouldBeVisible();

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

        LocalBroadcastManager.getInstance(this).registerReceiver(localMessageReciever,
                new IntentFilter("custom-event-name"));

        initiateAlarm();
        rp = new RequestPermissions(HomeActivity.this);
    }

    private void checkIfUpdateButtonsShouldBeVisible()
    {
        cav = new CheckAppVersion();
        cav.checkAllApps(HomeActivity.this);

        Log.d("checkapks", "1");
        if(cav.returnLauncher() == true)
        {
            Log.d("checkapks", "2");
            ImageButton LauncherButton = (ImageButton) findViewById(R.id.installLauncher);
            LauncherButton.setVisibility(View.VISIBLE);
        }
        if(cav.returnMovieMenu() == true)
        {
            Log.d("checkapks", "3");
            ImageButton MovieMenuButton = (ImageButton) findViewById(R.id.installMovieMenu);
            MovieMenuButton.setVisibility(View.VISIBLE);
        }

        Log.d("checkapks", "4");
        cav.reset();
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
        Uri tempUri = Uri.parse(movieMenuApkPath);
        Long tempID = DownloadData(tempUri, "MovieMenu.apk");
        queueID.add(tempID);
    }

    public void downloadLauncher()
    {
        Uri tempUri = Uri.parse(launcherApkPath);
        Long tempID = DownloadData(tempUri, "Launcher.apk");
        queueID.add(tempID);
    }

    private void prepareLauncherUpdateButton(String path)
    {
        ImageButton LayoutButton = (ImageButton) findViewById(R.id.installLauncher);
        LayoutButton.setVisibility(View.GONE);
        rp = new RequestPermissions(HomeActivity.this);

        sal = new SaveAndLoad();

        try{
            String launcherAPK = sal.Load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Launcher.txt");
            File file = new File(launcherAPK);
            boolean result = file.delete();
        }
        catch(Exception e)
        {
            Log.d("Error", "Deleting previous apk file..." + e);
        }

        sal.Save(path, "Launcher.txt");

        launcherPath = path;

        LayoutButton.setVisibility(View.VISIBLE);
    }

    private void prepareMovieMenuUpdateButton(String path)
    {
        sal = new SaveAndLoad();

        try{
            String movieMenuAPK = sal.Load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/MovieMenu.txt");
            File file = new File(movieMenuAPK);
            boolean result = file.delete();
        }
        catch(Exception e)
        {
            Log.d("Error", "Deleting previous apk file..." + e);
        }

        sal.Save(path, "MovieMenu.txt");

        movieMenuPath = path;
    }

   public void updateLauncher(View view)
    {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(new File(launcherPath)), type);
        startActivity(install);
    }

    public void updateMovieMenu(View view)
    {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(new File(movieMenuPath)), type);
        startActivity(install);
    }

    //Function that initiates the Android Download Manager class.
    //This class allows us to download files and display the download queue, without having to
    //create the functionality ourself.
    public long DownloadData (Uri uri, String name) {

        long downloadReference; //Reference to the object to be downloaded

        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE); //A reference to the download class
        DownloadManager.Request request = new DownloadManager.Request(uri); //The file to download

        //Setting title of request
        request.setTitle("Downloading " + name); //Title of the download to be displayed on the phone

        //Setting description of request
        request.setDescription("Movie Menu update"); //Description of the download

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(HomeActivity.this, Environment.DIRECTORY_DOWNLOADS, name); //Saveposition of APK

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

            if(message == "update movieMenu")
            {
                downloadMovieMenu();
            }
            if(message == "update launcher")
            {
                downloadLauncher();
            }
        }
    };

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                DownloadManager.Query DownloadQuery = new DownloadManager.Query();
                DownloadQuery.setFilterById(queueID.get(0));

                Cursor cursor = downloadManager.query(DownloadQuery);

                if (cursor.moveToFirst())
                {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1));
                    cursor.moveToFirst();

                    //This is String I pass to openFile method
                    String savedFilePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));

                    //installDownloadedAPK(savedFilePath);


                    Log.d("test", "1 " + savedFilePath);

                    if(savedFilePath.contains("Launcher"))
                        prepareLauncherUpdateButton(savedFilePath);
                    if(savedFilePath.contains("MovieMenu"))
                        prepareMovieMenuUpdateButton(savedFilePath);
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
    }
}
