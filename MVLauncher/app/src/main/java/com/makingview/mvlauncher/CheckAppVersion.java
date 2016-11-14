package com.makingview.mvlauncher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class CheckAppVersion
{
    ReadXmlFile rxf;
    SaveAndLoad sav;
    int movieMenuVersion;
    int launcherVersion;

    int xmlMovieMenuVersion;
    int xmlLauncherVersion;

    int currentVersion = 0;
    String result = "";

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

    public void checkNewVsOldData(Context context, int movieMenuValue, int launcherValue)
    {
        sav = new SaveAndLoad();
        String launcherAPK;
        String movieMenuAPK;
        try
        {
            launcherAPK = sav.Load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Launcher.txt");
            final PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(launcherAPK, 0);

            if(info.versionCode < launcherValue)
            {
                updateLauncher = true;
            }
            else
                updateLauncher = false;
        }
        catch(Exception e) //It failed somewhere, so just redownload the file
        {
            updateLauncher = true;
        }

        try
        {
            movieMenuAPK = sav.Load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/MovieMenu.txt");
            final PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(movieMenuAPK, 0);

            Log.d("info", "code " + info.versionCode);
            Log.d("info", "name" + info.versionName);

            if(info.versionCode < movieMenuValue) //This should probably be versionName instead
            {
                updateMovieMenu = true;
            }
            else
                updateMovieMenu = false;
        }
        catch(Exception e) //It failed somewhere, so just redownload the file
        {
            updateMovieMenu = true;
        }
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
