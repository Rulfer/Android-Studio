package com.makingview.mvlauncher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class CheckAppVersion
{
    ReadXmlFile rxf;
    int movieMenuVersion;
    int launcherVersion;

    int xmlMovieMenuVersion;
    int xmlLauncherVersion;

    public volatile boolean doneCheking = false;

    private boolean updateMovieMenu = false;
    private boolean updateLauncher = false;

    public boolean returnMovieMenu()
    {
        return  updateMovieMenu;
    }

    public boolean returnLauncher()
    {
        return  updateLauncher;
    }

    public void reset()
    {
        doneCheking = false;

        updateMovieMenu = false;
        updateLauncher = false;
    }

    public void checkAllApps(Context context,int movieValue, int launcherValue)
    {
        xmlMovieMenuVersion = movieValue;
        xmlLauncherVersion = launcherValue;

        movieMenuVersion = checkMovieMenu(context);
        launcherVersion = checkLauncher(context);

        if(movieMenuVersion < xmlMovieMenuVersion)
            updateMovieMenu = true;
        else
            updateMovieMenu = false;

        if(launcherVersion < xmlLauncherVersion)
            updateLauncher = true;
        else
            updateLauncher = false;

        Log.d("installed Menu", "" + movieMenuVersion);
        Log.d("installed Launcher", "" + launcherVersion);

        Log.d("update menu", "" + updateMovieMenu);
        Log.d("update launcher", "" + updateLauncher);

        doneCheking = true;
    }

    private int checkMovieMenu(Context context)
    {
        int temp;
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

    private int checkLauncher(Context context)
    {
        int temp;
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
