package makingview.com.apkdownloader;

import android.app.Activity;
import android.content.Context;
import android.icu.util.Output;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Bård on 28.10.2016.
 */

public class SaveAndLoad extends AppCompatActivity
{
    final static String fileName = "data.txt";
    //final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/instinctcoder/readwrite/" ;
    final static String path = Environment.getDataDirectory().getAbsolutePath();

    private String pathString = null;

    public SaveAndLoad(String path)
    {
        this.pathString = path;
    }

    public static boolean Save(String data)
    {

       try
       {
            File temp = new File(Environment.DIRECTORY_DOWNLOADS);
            if(!temp.exists())
                temp.mkdirs();
            File file = new File("/sdcard/Download/data.txt");
            /*if (!file.exists()) {
                Log.d("save tester", "file does not exist");
                file.createNewFile();
                Log.d("save tester", "file should exist now");
            }*/
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());

            return true;
        }
       catch(FileNotFoundException ex)
       {
            Log.d("save tester", ex.getMessage());
       }
       catch(IOException ex)
       {
            Log.d("save tester", ex.getMessage());
       }
        return  false;
    }

    /*public static void Save(File file, String[] data)
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                for (int i = 0; i<data.length; i++)
                {
                    fos.write(data[i].getBytes());
                    if (i < data.length-1)
                    {
                        fos.write("\n".getBytes());
                    }
                }
            }
            catch (IOException e) {e.printStackTrace();}
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e) {e.printStackTrace();}
        }
    }

    public static String[] Load(File file)
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String test;
        int anzahl=0;
        try
        {
            while ((test=br.readLine()) != null)
            {
                anzahl++;
            }
        }
        catch (IOException e) {e.printStackTrace();}

        try
        {
            fis.getChannel().position(0);
        }
        catch (IOException e) {e.printStackTrace();}

        String[] array = new String[anzahl];

        String line;
        int i = 0;
        try
        {
            while((line=br.readLine())!=null)
            {
                array[i] = line;
                i++;
            }
        }
        catch (IOException e) {e.printStackTrace();}
        return array;
    }*/
}
