package com.bard.mountedstoragepath;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity
{
    private RequestPermissions rp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rp = new RequestPermissions(MainActivity.this); //Request permissions

        boolean writable = isExternalStorageWritable();
        boolean readable = isExternalStorageReadable();

        Log.d("Externalstorage", "Writable? " + writable);
        Log.d("Externalstorage", "readable? " + readable);

        File storageDir = null;
        Context context = MainActivity.this;

        storageDir = context.getFilesDir();
        Log.d("Externalstorage", storageDir.getAbsolutePath());

        TextView Readable = (TextView) findViewById(R.id.readable);
        Readable.setText("Readable? " + readable);

        TextView Writable = (TextView) findViewById(R.id.writable);
        Writable.setText("Writable? " + writable);

        //String path = getApplicationContext().getExternalFilesDirs();

        //Context context = ;
        //context.getExternalFilesDirs();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
