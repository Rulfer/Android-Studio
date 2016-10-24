package makingview.com.apkdownloader;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class StartActivity extends AppCompatActivity implements  View.OnClickListener {
    private DownloadManager downloadManager;

    private List<Long> queueIDs = new ArrayList<>();


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Button DownloadMenu = (Button) findViewById(R.id.download_menu);
        DownloadMenu.setOnClickListener(this);

        Button DownloadPano = (Button) findViewById(R.id.download_pano);
        DownloadPano.setOnClickListener(this);

        Button CancelDownload = (Button) findViewById(R.id.cancel_download);
        CancelDownload.setOnClickListener(this);
        CancelDownload.setEnabled(false);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }

    public void installDownloadedAPK()
    {
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        destination += apkName;
        final Uri uri = Uri.parse("file://" + destination);
       Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri,
                        "application/vnd.android.package-archive");
        startActivity((promptInstall));
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.download_menu:
                Uri menu_uri = Uri.parse("https://content.makingview.com/apks/MovieMenu.apk");
<<<<<<< HEAD
                apkName = "MovieMenu.apk";
                queueIDs.add(DownloadData(menu_uri, view));
=======
                names.add("MovieMenu.apk");

                if(counter == 0) {
                    queueID = (DownloadData(menu_uri, view));
                }
                else {
                    queueUri.add(menu_uri);
                    queueView.add(view);
                }
                counter++;
>>>>>>> parent of 80ae4a5... Revert "Shit is broken now"
                break;
            case R.id.download_pano:
                Uri pano_uri = Uri.parse("https://content.makingview.com/apks/panoaudio.apk");
<<<<<<< HEAD
                apkName = "PanoAudio.apk";
                queueIDs.add(DownloadData(pano_uri, view));
=======
                names.add("panoaudio.apk");

                if(counter == 0) {
                    queueID = (DownloadData(pano_uri, view));
                }
                else {
                    queueUri.add(pano_uri);
                    queueView.add(view);
                }
                counter++;
>>>>>>> parent of 80ae4a5... Revert "Shit is broken now"
                break;
            case R.id.cancel_download:
<<<<<<< HEAD
                downloadManager.remove(queueIDs.get(0));
=======
                downloadManager.remove(queueID);
>>>>>>> parent of 80ae4a5... Revert "Shit is broken now"
        }
    }

    private long DownloadData (Uri uri, View v) {

        long downloadReference;

        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Downloading " + names.get(0));

        //Setting description of request
        request.setDescription("Android Data download using DownloadManager.");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(StartActivity.this, Environment.DIRECTORY_DOWNLOADS, names.get(0)); //Saveposition of APK


        //Enqueue download and save the referenceId
        downloadReference = downloadManager.enqueue(request);

        View cancelButton = findViewById(R.id.cancel_download);
        cancelButton.setEnabled(true);
        cancelButton.setVisibility(View.VISIBLE);

        return downloadReference;
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
<<<<<<< HEAD

            //check if the broadcast message is for our Enqueued download
            /*long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                Query query = new Query();
                query.setFilterById(queueIDs.get(0));
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c
                            .getInt(columnIndex)) {

                        Log.d("....", "did you just download the file? : O ");

                        installDownloadedAPK();

                        Integer uriIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);

                    }
                }
            }*/

            /*DownloadManager.Query DownloadQuery = new DownloadManager.Query();
            DownloadQuery.setFilterById(queueIDs.get(0));
=======
            DownloadManager.Query DownloadQuery = new DownloadManager.Query();
            DownloadQuery.setFilterById(queueID);
>>>>>>> parent of 80ae4a5... Revert "Shit is broken now"

            Cursor cursor = downloadManager.query(DownloadQuery);
            if(cursor.moveToFirst())
            {
<<<<<<< HEAD

                DownloadStatus(cursor, queueIDs.get(0));
            }*/

            Toast toast = Toast.makeText(StartActivity.this,
                    "Download Complete", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();


            queueIDs.remove(0);
            if(queueIDs.size() == 0)
=======
                DownloadStatus(cursor, queueID);
                Toast toast = Toast.makeText(StartActivity.this,
                        "Download Complete", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
                downloadManager.remove(queueID);
            }
            else
            {
                Toast toast = Toast.makeText(StartActivity.this,
                        "Download Cancelled", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }

            counter--;
            names.remove(0);

            if(counter == 0)
>>>>>>> parent of 80ae4a5... Revert "Shit is broken now"
            {
                Button CancelDownload = (Button) findViewById(R.id.cancel_download);
                CancelDownload.setEnabled(false);
                CancelDownload.setVisibility(View.GONE);
<<<<<<< HEAD
=======
            }
            else
            {
                queueID = (DownloadData(queueUri.get(0), queueView.get(0)));
>>>>>>> parent of 80ae4a5... Revert "Shit is broken now"
            }
        }
    };

    public void showDownload(View view) {
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
    }

    /*private void DownloadStatus(Cursor cursor, long DownloadId){

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";*/

/*
        switch(status){
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch(reason){
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch(reason){
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                reasonText = "PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                reasonText = "RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "SUCCESS";
                //reasonText = "Filename:\n" + filename;
<<<<<<< HEAD
=======
                installDownloadedAPK();
>>>>>>> parent of 80ae4a5... Revert "Shit is broken now"
                break;
        }

        Log.d("STATUS: ", reasonText);
    }*/

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Start Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}


