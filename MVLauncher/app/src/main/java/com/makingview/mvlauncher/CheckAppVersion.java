package com.makingview.mvlauncher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class CheckAppVersion
{
    float movieMenuVersion;
    float launcherVersion;

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
