package com.makingview.widgetexample;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class SaveAndLoad
{
    final static String fileName = "data.txt";
    //final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/instinctcoder/readwrite/" ;
    final static String path = Environment.getDataDirectory().getAbsolutePath();

    private String pathString = null;

    public SaveAndLoad(String path)
    {
        this.pathString = path;
    }

    public void Save(String data)
    {
        try
        {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            File createPath = new File(path);

            if(!createPath.exists())
                createPath.mkdirs();

            String fileName = "/sav.data";
            File file = new File(path + fileName);


            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println(data);
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

    public String Load()
    {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        String path = root + "/sav.data";
        try

        {
            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
            System.out.print(content);
            return content;
        }
        catch(IOException e)
        {
            return "error";
        }
    }
}
