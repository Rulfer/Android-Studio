package makingview.com.apkdownloader;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

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
            //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
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

    /*public static String[] Load(File file)
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
