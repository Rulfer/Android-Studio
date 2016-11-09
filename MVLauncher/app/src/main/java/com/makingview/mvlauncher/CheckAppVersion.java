package com.makingview.mvlauncher;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Bård on 09.11.2016.
 */

public class CheckAppVersion
{
    float movieMenuVersion;
    float launcherVersion;
    /*@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkMovieMenu();
    }*/

    public void checkAllApps(Context context)
    {

        movieMenuVersion = checkMovieMenu(context);
        launcherVersion = checkLauncher(context);
    }

    private float checkMovieMenu(Context context)
    {
        float temp;
        String pName = "com.MakingView.movieMenuNew";
        PackageManager pm = context.getPackageManager();
        try
        {
            PackageInfo info = pm.getPackageInfo(pName, 0);
            Log.d("Checkig movie menu", "Info: " + info.versionCode);
            temp = info.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.d("Checkig movie menu", "failed");
            temp = 0;
        }

        return temp;
    }

    private float checkLauncher(Context context)
    {
        float temp;
        PackageManager pm = context.getPackageManager();

        try
        {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            Log.d("Checkig launcher", "Info: " + info.versionName);
            temp = info.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.d("Checkig launcher", "failed");
            temp = 0;
        }

        return temp;
    }
}
