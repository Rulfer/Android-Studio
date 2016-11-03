package com.makingview.widgetexample;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class DownloadClass
{
    private DownloadManager downloadManager;
    Long queueID;

    public DownloadClass(Uri uri, Context context)
    {
        queueID = DownloadData(uri, context);
    }

    private long DownloadData (Uri uri, Context context) {

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
                installDownloadedAPK(savedFilePath, context);
                Toast toast = Toast.makeText(context,
                        "Download completed", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
            else
            {
                Toast toast = Toast.makeText(context,
                        "Download Cancelled", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
    };

    public void installDownloadedAPK(String fileName, Context context)
    {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl((fileName));
        String type = map.getMimeTypeFromExtension(ext);

        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(new File(fileName)), type);
        context.startActivity(install);
    }
}
