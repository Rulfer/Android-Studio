package com.makingview.mvlauncher;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReadXmlFile
{
    CheckAppVersion cav;

    String urlString = "http://video.makingview.no/apps/gearVR/apkinfo.xml";

    private XmlPullParserFactory xmlFactoryObject;

    public volatile boolean parsingComplete = true;
    public volatile boolean downloadFailed = false;

    private int movieMenuVersion = 0;
    private int launcherVersion = 0;

    public int returnMovieMenuVersion()
    {
        return movieMenuVersion;
    }
    public int returnLauncherVersion()
    {
        return launcherVersion;
    }

    public void reset()
    {
        parsingComplete = true;
        downloadFailed = false;
    }

    public void fetchXML(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
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

    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;
        try {
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
                        if(name.equals("movieMenuVersion"))
                        {
                            Log.d("found moviemenuversion", text);
                            movieMenuVersion = Integer.parseInt(text);
                        }
                        if(name.equals("launcherVersion"))
                        {
                            Log.d("found launcherversion", text);
                            launcherVersion = Integer.parseInt(text);
                        }
                        break;
                }
                event = myParser.next();
            }
            parsingComplete = false;
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
