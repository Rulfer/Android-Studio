package com.makingview.mvlauncher;

import android.app.Activity;
import android.app.DownloadManager;
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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Bård on 08.11.2016.
 */

public class HomeActivity extends Activity
{
    RequestPermissions rp;
    CheckAppVersion cav;

    private String downloadPath = "https://content.makingview.com/apks/MovieMenu.apk";
    private String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
    private DownloadManager downloadManager;
    Long queueID;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

        cav = new CheckAppVersion();
        cav.checkAllApps(HomeActivity.this);

        downloadMovieMenu();
    }

    public void openMovieMenu(View view) {
        PackageManager manager = getPackageManager();

        Intent i = manager.getLaunchIntentForPackage("com.MakingView.movieMenuNew");

        if (i != null) {
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        }
    }

    public void downloadMovieMenu()
    {
        Uri tempUri = Uri.parse(downloadPath);
        queueID = DownloadData(tempUri);
    }

    public void installDownloadedAPK(String fileName)
    {
        Log.d("fileName" , fileName);
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl((fileName));
        String type = map.getMimeTypeFromExtension(ext);

        Intent install = new Intent(Intent.ACTION_VIEW);
        Log.d("fack off", "openFile Trying to open file: " + Uri.fromFile(new File(fileName)));
        install.setDataAndType(Uri.fromFile(new File(fileName)), type);
        startActivity(install);
    }

    public long DownloadData (Uri uri) {

        long downloadReference;

        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Downloading " + "MovieMenu.apk");

        //Setting description of request
        request.setDescription("Android Data download using DownloadManager.");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(HomeActivity.this, Environment.DIRECTORY_DOWNLOADS, "MovieMenu.apk"); //Saveposition of APK



        //Enqueue download and save the referenceId
        downloadReference = downloadManager.enqueue(request);

        /*View cancelButton = findViewById(R.id.cancel_download);
        cancelButton.setEnabled(true);
        cancelButton.setVisibility(View.VISIBLE);

        Button LayoutButton = (Button) findViewById(R.id.lay_below_me);
        LayoutButton.setVisibility(View.INVISIBLE);*/

        return downloadReference;
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query DownloadQuery = new DownloadManager.Query();
            DownloadQuery.setFilterById(queueID);

            Cursor cursor = downloadManager.query(DownloadQuery);
            if(cursor.moveToFirst())
            {
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
            }
            else
            {
                Toast toast = Toast.makeText(HomeActivity.this,
                        "Download Cancelled", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
    };
}
