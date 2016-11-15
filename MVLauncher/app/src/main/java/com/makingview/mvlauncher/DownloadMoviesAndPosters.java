package com.makingview.mvlauncher;

import android.os.Environment;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bård on 15.11.2016.
 */

public class DownloadMoviesAndPosters
{
    List<String> localMovies = new ArrayList<>();
    List<String> localPosters = new ArrayList<>();
    List<String> names = new ArrayList<>();
    List<String> downloadLinks = new ArrayList<>();

    List<String> moviesToDownload = new ArrayList<>();
    List<String> postersToDownload = new ArrayList<>();

    String movieFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString();
    String xmlPath = "http://video.makingview.no/apps/gearVR/makingview.xml";

    private XmlPullParserFactory xmlFactoryObject;

    public volatile boolean parsingComplete = true;
    public volatile boolean downloadFailed = false;

    public List<String> returnMovies()
    {
        return moviesToDownload;
    }

    public List<String> returnPosters()
    {
        return postersToDownload;
    }

    public void scanForMoviesAndPosters()
    {
        File folder = new File(movieFolder);

        if(!folder.exists())
            folder.mkdirs();

        File[] files = folder.listFiles();

        try{
            localMovies.clear();
            localPosters.clear();
        }
        catch (Exception e)
        {
            Log.d("Error clearing lists", e.toString());
        }

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

        fetchXML();
    }

    private void fetchXML(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run()
            {
                try {
                    URL url = new URL(xmlPath);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream stream = conn.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(myparser);
                    stream.close();
                }
                catch (Exception e) {
                    downloadFailed = true;
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;
        try
        {
            try{
                names.clear();
                downloadLinks.clear();
            }
            catch (Exception e)
            {
                Log.d("Error clearing lists", e.toString());
            }

            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT && downloadFailed == false) {
                String name=myParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("name"))
                        {
                            names.add(text);
                        }
                        if(name.equals("url"))
                        {
                            downloadLinks.add(text);
                        }
                        break;
                }
                event = myParser.next();
            }
            compareLists();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compareLists()
    {
        boolean foundMovie = false;
        boolean foundPoster = false;

        for(int i = 0; i < names.size(); i++)
        {
            String movieName = names.get(i) + ".m-experience";
            for(int j = 0; j < localMovies.size(); j++)
            {
                if(movieName.equals(localMovies.get(j)))
                {
                    Log.d("FOUND", movieName);
                    foundMovie = true;
                }
            }

            String posterName = names.get(i) + ".p-experience";
            for(int j = 0; j < localPosters.size(); j++)
            {
                if(posterName.equals(localPosters.get(j)))
                {
                    Log.d("FOUND", posterName);
                    foundPoster = true;
                }
            }

            if(!foundMovie)
            {
                Log.d("MISSING", movieName);
                Log.d("Download link", downloadLinks.get(i) + ".m-experience");
                moviesToDownload.add(downloadLinks.get(i) + ".m-experience");
            }
            if(!foundPoster)
            {
                Log.d("MISSING", posterName);
                Log.d("Download link", downloadLinks.get(i) + ".p-experience");
                postersToDownload.add(downloadLinks.get(i) + ".p-experience");
            }

            foundMovie = false;
            foundPoster = false;
        }

        parsingComplete = false;
    }
}
