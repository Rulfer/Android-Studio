package makingview.com.apkdownloader;


import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class StartActivity extends AppCompatActivity implements  View.OnClickListener {
    private DownloadManager downloadManager;
    private ReadXmlFile obj;
    private SaveAndLoad sl;
    private RequestPermissions rp;

    private String xmlUrl = "http://video.makingview.no/apps/gearVR/";
    private String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();

    Long queueID;
    private List<String> names = new ArrayList<>();
    private List<Uri> queueUri = new ArrayList<>();
    private List<View> queueView = new ArrayList<>();

    ArrayList<Button> myButtons = new ArrayList<>();

    private List<String> addedNames = new ArrayList<>();
    private List<String> addedUrls = new ArrayList<>();
    private String tempUrl = "";
    private String tempName ="";

    Integer counter = 0;
    Boolean userPromptedToWriteNewCode = false;

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

        Button CodeButton = (Button) findViewById(R.id.code_input);
        CodeButton.setOnClickListener(this);

        Button LayoutButton = (Button) findViewById(R.id.lay_below_me);
        LayoutButton.setEnabled(false);
        LayoutButton.setVisibility(View.GONE);


        final EditText editText = (EditText) findViewById(R.id.edit_code);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent){
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_DONE)
                {
                    Log.d("asdsadm", "asdasd");
                    String inputText = textView.getText().toString();

                    editText.setEnabled(false);
                    editText.setVisibility(View.GONE);

                    Button ShowInput = (Button) findViewById(R.id.code_input);
                    ShowInput.setVisibility(View.VISIBLE);

                    Button LayoutButton = (Button) findViewById(R.id.lay_below_me);
                    LayoutButton.setVisibility(View.GONE);
                    getXmlDoc(inputText);
                }

                return handled;
            }
        });

        editText.setVisibility(View.GONE);

        //cancelAlarm();

        rp = new RequestPermissions(StartActivity.this); //Request permissions
        findSave(); //Check for save file

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }

    private void findSave()
    {
        sl = new SaveAndLoad(savePath);
        String result = sl.Load(savePath + "/sav.data");

        if(result == "error")
        {
            downloadFailed();
        }
        else
            getXmlDoc(result);
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

            sl = new SaveAndLoad(savePath);
            sl.Save(code);
        }
        else {
            if(!userPromptedToWriteNewCode)
                downloadFailed();
            else
                closeInputField();
        }
    }

    private void downloadFailed()
    {
        final EditText editText = (EditText) findViewById(R.id.edit_code);
        editText.setEnabled(true);
        editText.setVisibility(View.VISIBLE);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        Button LayoutButton = (Button) findViewById(R.id.lay_below_me);
        LayoutButton.setVisibility(View.INVISIBLE);
        Button InputButton = (Button) findViewById(R.id.code_input);
        InputButton.setVisibility(View.GONE);
    }

    private void closeInputField()
    {
        final EditText editText = (EditText) findViewById(R.id.edit_code);
        editText.setEnabled(false);
        editText.setVisibility(View.GONE);

        Button LayoutButton = (Button) findViewById(R.id.lay_below_me);
        LayoutButton.setEnabled(false);
        LayoutButton.setVisibility(View.GONE);
        Button InputButton = (Button) findViewById(R.id.code_input);
        InputButton.setVisibility(View.VISIBLE);
        userPromptedToWriteNewCode = false;
    }

    private void createButtons() {
        for (int i = 0; i < myButtons.size(); i++)
        {
            myButtons.get(i).setVisibility(View.GONE);
        }
        myButtons.clear();
        
        final Button TopButton = (Button) findViewById(R.id.lay_below_me);
        myButtons.add(TopButton);
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
                Button LayoutButton = (Button) findViewById(R.id.lay_below_me);
                LayoutButton.setEnabled(false);
                LayoutButton.setVisibility(View.GONE);
                break;

            case R.id.code_input:
                userPromptedToWriteNewCode = true;
                downloadFailed();
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

        Button LayoutButton = (Button) findViewById(R.id.lay_below_me);
        LayoutButton.setVisibility(View.INVISIBLE);

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

            Button LayoutButton = (Button) findViewById(R.id.lay_below_me);
            LayoutButton.setEnabled(false);
            LayoutButton.setVisibility(View.GONE);

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

        initiateAlarm();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cancelAlarm(StartActivity.this);
    }

    private void initiateAlarm()
    {
        // time at which alarm will be scheduled here alarm is scheduled at 1 day from current time,
        // we fetch  the current time in milliseconds and added 1 day time
        // i.e. 24*60*60*1000= 86,400,000   milliseconds in a day

        //Long time = new GregorianCalendar().getTimeInMillis()+24*60*60*1000;
        Long time = new GregorianCalendar().getTimeInMillis()+60000 * 2;
        // create an Intent and set the class which will execute when Alarm triggers, here we have
        // given AlarmReciever in the Intent, the onRecieve() method of this class will execute when
        // alarm triggers and
        //we call the method inside onRecieve() method pf Alarmreciever class
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);

        // create the object
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(this, "Alarm Scheduled for Tommrrow", Toast.LENGTH_LONG).show();
    }

    private void cancelAlarm(Context context)
    {
        AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
}


