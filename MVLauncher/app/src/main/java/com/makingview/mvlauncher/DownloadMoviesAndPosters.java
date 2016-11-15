package com.makingview.mvlauncher;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bård on 15.11.2016.
 */

public class DownloadMoviesAndPosters
{
    List<String> localMovies = new ArrayList<>();
    List<String> localPosters = new ArrayList<>();

    String movieFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString();

    public void scanForMoviesAndPosters()
    {
        File folder = new File(movieFolder);

        if(!folder.exists())
            folder.mkdirs();

        File[] files = folder.listFiles();

        try{
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isFile())
                {
                    if (files[i].getName().contains(".m-experience")) {
                        Log.d("found movie", files[i].getName());
                        localMovies.add(files[i].getName());
                    }
                    if (files[i].getName().contains(".p-experience")) {
                        Log.d("found poster", files[i].getName());
                        localPosters.add(files[i].getName());
                    }
                }
            }
        }
        catch(NullPointerException e)
        {
            Log.d("Scan error", e.toString());
        }
    }
}
