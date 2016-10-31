package makingview.com.apkdownloader;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class StartActivity extends AppCompatActivity implements  View.OnClickListener {
    private DownloadManager downloadManager;
    private ReadXmlFile obj;
    private SaveAndLoad sl;
    //private String xmlUrl = "http://video.makingview.no/apps/gearVR/makingview.xml";
    private String xmlUrl = "http://video.makingview.no/apps/gearVR/";
    private String savePath = Environment.DIRECTORY_DOCUMENTS;

    //private List<Long> queueIDs = new ArrayList<>();
    Long queueID;
    private List<String> names = new ArrayList<>();
    private List<Uri> queueUri = new ArrayList<>();
    private List<View> queueView = new ArrayList<>();

    private List<String> addedNames = new ArrayList<>();
    private List<String> addedUrls = new ArrayList<>();
    private String tempUrl = "";
    private String tempName ="";

    Integer counter = 0;
    Uri storedUri;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

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

        Button CancelDownload = (Button) findViewById(R.id.cancel_download);
        CancelDownload.setOnClickListener(this);
        CancelDownload.setEnabled(false);

        final EditText editText = (EditText) findViewById(R.id.edit_code);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent){
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_DONE)
                {
                    String inputText = textView.getText().toString();

                    editText.setEnabled(false);
                    editText.setVisibility(View.GONE);
                    getXmlDoc(inputText);

                }

                return handled;
            }
        });

        verifyStoragePermissions(StartActivity.this);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private boolean findSave()
    {
        File file = new File(savePath + "/sav.data");
        if(file.exists())
        {
            //sl = new SaveAndLoad(savePath + "/sav.data");
            //sl.Load(file);
            return true;
        }
        else
            return false;
    }

    private void getXmlDoc(String code)
    {
        String newPath = xmlUrl + code + ".xml";
        obj = new ReadXmlFile(newPath);
        obj.fetchXML();

        while(obj.parsingComplete && obj.downloadFailed == false) {
            addedNames = new ArrayList<>(obj.getNames());
            addedUrls = new ArrayList<>(obj.getUrls());
        }

        if(obj.downloadFailed == false)
        {
            createButtons();

            //sl = new SaveAndLoad(savePath);
            //sl.Save(code);
            Save(code);
            //String[] saveText = String.valueOf(code).split(System.getProperty("line.separator"));
            //File file = new File(path);
            //sl = new SaveAndLoad(path);
            //sl.Save(file, saveText);
        }
        else
            downloadFailed();
    }

    public void Save(String data)
    {
        try
        {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

            String fileName = "/sav.data";
            File file = new File(path + fileName);

            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println("hello, world");
            writer.close();
            Log.d("save tester", "SAVED!");
        }
        catch(FileNotFoundException ex)
        {
            Log.d("save tester", ex.getMessage());
        }
        catch(Exception e)
        {
            Log.d("save tester", e.getMessage());
        }
    }

    private void downloadFailed()
    {
        Log.d("asdasd", "HEY");

        final EditText editText = (EditText) findViewById(R.id.edit_code);
        editText.setEnabled(true);
        editText.setVisibility(View.VISIBLE);

    }

    private void createButtons() {
        ArrayList<Button> myButtons = new ArrayList<>();
        final Button CancelDownload = (Button) findViewById(R.id.cancel_download);
        myButtons.add(CancelDownload);
        for(int i = 0; i < addedNames.size(); i++)
        {
            tempName = addedNames.get(i);
            tempUrl = addedUrls.get(i);
            System.out.println(tempName);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            p.addRule(RelativeLayout.BELOW, myButtons.get(0).getId());

            Button btn = new Button(this);
            myButtons.add(btn);
            btn.setText("Videosize: " + tempName + "Mb");
            btn.setId(i);
            btn.setHeight(230);
            final int id_ = btn.getId();

            LinearLayout layout = (LinearLayout) findViewById(R.id.button_layout);
            layout.setLayoutParams(p);

            layout.addView(btn, params);

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Uri urlUri = Uri.parse(tempUrl);
                    names.add(tempName);
                    if(counter == 0)
                        queueID = (DownloadData(urlUri, view));
                    else
                    {
                        queueUri.add(urlUri);
                        queueView.add(view);
                    }
                    counter++;
                }
            });
        }

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

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.cancel_download:
                downloadManager.remove(queueID);
                Button CancelDownload = (Button) findViewById(R.id.cancel_download);
                CancelDownload.setEnabled(false);
                CancelDownload.setVisibility(View.GONE);
                break;
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


            Button CancelDownload = (Button) findViewById(R.id.cancel_download);
            CancelDownload.setEnabled(false);
            CancelDownload.setVisibility(View.GONE);

            if(counter > 0)
            {
                queueID = (DownloadData(queueUri.get(0), queueView.get(0)));
            }

            if(queueUri.size() > 0)
            {
                queueUri.remove(0);
                queueView.remove(0);
            }
        }
    };

    public void showDownload(View view) {
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
    }

    private void DownloadStatus(Cursor cursor, long DownloadId)
    {
        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        //int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        //String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch(status){
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
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
                break;

        }
    }

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

    class Member
    {
        boolean downloaded = false;
        String localPath = "";
        String name;
        Uri uri;
        View view;
    }
}


