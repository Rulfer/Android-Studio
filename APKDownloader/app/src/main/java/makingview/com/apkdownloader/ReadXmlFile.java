package makingview.com.apkdownloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReadXmlFile
{
    StartActivity sa;
    private List<String> names = new ArrayList<>();
    private List<String> urls = new ArrayList<>();

    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    public volatile boolean downloadFailed = false;

    public ReadXmlFile(String url){
        this.urlString = url;
    }

    public List<String> getNames() {
        return names;
    }

    public List<String> getUrls(){
        return urls;
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
                        Log.d("END_TAG", text);
                        if(name.equals("videosize"))
                            names.add(text);
                        if(name.equals("url"))
                        {
                            String temp = text + ".p-experience";
                            System.out.println(temp);
                            urls.add(temp);
                        }

                        /*if(name.equals("country")){
                            country = text;
                        }

                        else if(name.equals("humidity")){
                            humidity = myParser.getAttributeValue(null,"value");
                        }

                        else if(name.equals("pressure")){
                            pressure = myParser.getAttributeValue(null,"value");
                        }

                        else if(name.equals("temperature")){
                            temperature = myParser.getAttributeValue(null,"value");
                        }

                        else{
                        }*/
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
}
