package com.makingview.mvlauncher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class CheckAppVersion
{
    ReadXmlFile rxf;
    float movieMenuVersion;
    float launcherVersion;

    float xmlMovieMenuVersion;
    float xmlLauncherVersion;

    public void checkAllApps(Context context, float movieValue, float launcherValue)
    {
        xmlMovieMenuVersion = movieValue;
        xmlLauncherVersion = launcherValue;
        Log.d("XML MovieMenu.apk", "version is: " + xmlMovieMenuVersion);
        Log.d("XML Launcher.apk", "version is: " + xmlLauncherVersion);

        movieMenuVersion = checkMovieMenu(context);
        launcherVersion = checkLauncher(context);
        //Log.d("Currently installed MovieMenu.apk", "version is: " + movieMenuVersion);
        Log.d("Installed MovieMenu.apk", "version is: " + movieMenuVersion);
        Log.d("Installed Launcer.apk", "version is: " + launcherVersion);
    }

    private float checkMovieMenu(Context context)
    {
        float temp;
        String pName = "com.MakingView.movieMenu";
        PackageManager pm = context.getPackageManager();
        try
        {
            PackageInfo info = pm.getPackageInfo(pName, 0);
            temp = info.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
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
            temp = info.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            temp = 0;
        }

        return temp;
    }
}
