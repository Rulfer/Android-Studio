package com.makingview.mvlauncher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

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
        String otherTest = sav.Load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Launcher.txt");
        final PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(otherTest, 0);
        Log.d("localapk", info.versionName + " || " + info.versionCode);
        int currentVersion;
        String result;
        result = sav.Load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/MovieMenuVersion.txt");
        if(result != "error")
        {
            currentVersion = Integer.parseInt(result);
            if(currentVersion < movieMenuValue) //The currently downloaded apk is outdatet
            {
                updateMovieMenu = true;
                sav.Save(Integer.toString(movieMenuValue), "MovieMenuVersion.txt");
            }
            else
                updateMovieMenu = false;
        }
        else //Either the file does not exist, or something else is wrong. Either way, redownload apk
        {
            updateMovieMenu = true;
            sav.Save(Integer.toString(movieMenuValue), "MovieMenuVersion.txt");
        }

        result = sav.Load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/LauncherVersion.txt");
        if(result != "error")
        {
            currentVersion = Integer.parseInt(result);
            if(currentVersion < launcherValue) //The currently downloaded apk is outdatet
            {
                updateLauncher = true;
                sav.Save(Integer.toString(launcherValue), "LauncherVersion.txt");
            }
            else
                updateLauncher = false;
        }
        else //Either the file does not exist, or something else is wrong. Either way, redownload apk
        {
            updateLauncher = true;
            sav.Save(Integer.toString(launcherValue), "LauncherVersion.txt");
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
