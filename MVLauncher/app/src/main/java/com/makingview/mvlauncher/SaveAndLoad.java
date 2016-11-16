package com.makingview.mvlauncher;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;
import java.util.Scanner;

public class SaveAndLoad
{
    //final static String fileName = "data.txt";
    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();

    public void Save(String input, String fileName)
    {
        try
        {
            File createPath = new File(path);

            if(!createPath.exists())
                createPath.mkdirs();

            File file = new File(path + "/" + fileName);

            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.print(input);
            writer.close();
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

    public String Load(String filePath)
    {
        try
        {
            String content = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
            System.out.print(content);
            return content;
        }
        catch(IOException e)
        {
            return "error";
        }
    }
}
