package com.makingview.mvlauncher;

import android.app.DownloadManager;
import android.os.Environment;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
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
    private List<String> localMovies = new ArrayList<>();
    private List<String> localPosters = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private List<String> downloadLinks = new ArrayList<>();

    private List<String> moviesToDownload = new ArrayList<>();
    private List<String> postersToDownload = new ArrayList<>();
    private List<String> moviesToDownloadNames = new ArrayList<>();
    private List<String> postersToDownloadNames = new ArrayList<>();

    private String movieFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString();

    private String xmlPath = "http://video.makingview.no/apps/gearVR/makingview.xml";

    private XmlPullParserFactory xmlFactoryObject;

    public volatile boolean parsingComplete = false;
    public volatile boolean downloadFailed = false;
    public volatile boolean codeFailed = false;

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
        try
        {
            File folder = new File(movieFolder);

            if (!folder.exists())
                folder.mkdirs();

            File[] files = folder.listFiles();

            localMovies.clear();
            localPosters.clear();

            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isFile())
                {
                    if (files[i].getName().contains(".m-experience")) {
                        localMovies.add(files[i].getName());
                    }
                    if (files[i].getName().contains(".p-experience")) {
                        localPosters.add(files[i].getName());
                    }
                }
            }
        }
        catch(NullPointerException e)
        {
            parsingComplete = true;
            codeFailed = true;
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
                catch (Exception e)
                {
                    codeFailed = true;
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

            names.clear();
            downloadLinks.clear();

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

        catch (Exception e)
        {
            codeFailed = true;
            parsingComplete = true;
            e.printStackTrace();
        }
    }

    private void compareLists()
    {
        try {
            boolean foundMovie = false;
            boolean foundPoster = false;

            int index = 0;

            for (int i = 0; i < names.size(); i++)
            {
                String movieName = names.get(i) + ".m-experience";
                String posterName = names.get(i) + ".p-experience";

                for (int j = 0; j < localMovies.size(); j++)
                {
                    if (movieName.equals(localMovies.get(j)))
                    {
                        foundMovie = true;
                    }
                }

                for (int j = 0; j < localPosters.size(); j++)
                {
                    if (posterName.equals(localPosters.get(j)))
                    {
                        foundPoster = true;
                    }
                }

                if(!foundMovie)
                {
                    moviesToDownload.add(downloadLinks.get(i) + ".m-experience");
                    moviesToDownloadNames.add(movieName);
                }
                if(!foundPoster)
                {
                    postersToDownload.add(downloadLinks.get(i) + ".p-experience");
                    postersToDownloadNames.add(posterName);
                }

                foundMovie = false;
                foundPoster = false;
            }

            /*for(String name:names)
            {
                String movieName = name + ".m-experience";
                String posterName = name + ".p-experience";

                for(String movName:localMovies)
                {
                    if(movieName.equals(movName)){foundMovie = true;}
                }

                for(String posName:localPosters)
                {
                    if(posName.equals(posName)){foundPoster = true;}
                }

                if (!foundMovie)
                {
                    moviesToDownload.add(downloadLinks.get(index) + ".m-experience");
                    moviesToDownloadNames.add(movieName);
                }
                if (!foundPoster) {
                    postersToDownload.add(downloadLinks.get(index) + ".p-experience");
                    postersToDownloadNames.add(posterName);
                }

                foundMovie = false;
                foundPoster = false;
                index ++;
            }*/
            parsingComplete = true;
        }
        catch(Exception e)
        {
            codeFailed = true;
            parsingComplete = true;
            e.printStackTrace();
        }
    }
}
