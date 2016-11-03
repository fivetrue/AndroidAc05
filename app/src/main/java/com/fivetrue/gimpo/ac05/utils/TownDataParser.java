package com.fivetrue.gimpo.ac05.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.fivetrue.gimpo.ac05.firebase.model.AppConfig;
import com.fivetrue.gimpo.ac05.firebase.model.TownNews;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by kwonojin on 2016. 10. 31..
 */

public class TownDataParser extends AsyncTask<Void, Void, ArrayList<TownNews>>{

    private static final String TAG = "TownDataParser";

    private static final String TAG_TABLE_BODY = "tbody";
    private static final String TAG_TABLE_ROW = "tr";
    private static final String TAG_TABLE_DATA = "td";
    private static final String TAG_HREF_LINK = "href";

    public interface OnLoadTownDataListener{
        void onLoad(List<TownNews> list);
    };

    private final Context context;
    private final AppConfig config;
    private URL url;
    private OnLoadTownDataListener onLoadTownDataListener;

    public TownDataParser(Context context, AppConfig config, OnLoadTownDataListener ll){
        this.context = context;
        this.config = config;
        this.onLoadTownDataListener = ll;
        try {
            this.url = new URL(config.townHostUrl + config.townPath + config.townBoardPath);
        } catch (MalformedURLException e) {
            Log.w(TAG, "TownDataParser: ", e);
        }
    }

    private Document getDocument(String htmlSource) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(htmlSource));
        Document doc = db.parse(is);
        return doc;
    }


    @Override
    protected ArrayList<TownNews> doInBackground(Void... params) {
        ArrayList<TownNews> townDataList = new ArrayList<>();
        String htmlText = "";
        try {
            String inputLine;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            while ((inputLine = in.readLine()) != null){
                htmlText += inputLine;
            };
            in.close();

            Document root = getDocument(htmlText);
            NodeList nodeList = root.getElementsByTagName(TAG_TABLE_BODY);
            if(nodeList != null && nodeList.getLength() > 0){
                Element table = (Element) nodeList.item(0);
                NodeList rows = table.getElementsByTagName(TAG_TABLE_ROW);
                if(rows != null && rows.getLength() > 0){
                    for(int rowCount = 0 ; rows.getLength() > rowCount ; rowCount ++){
                        Element row = (Element) rows.item(rowCount);
                        NodeList datas = row.getElementsByTagName(TAG_TABLE_DATA);
                        if(datas != null && datas.getLength() > 0){
                            Element number = (Element) datas.item(0);
                            Element title = (Element) datas.item(1);
                            Element author = (Element) datas.item(3);
                            Element date = (Element) datas.item(4);
                            Element readCount = (Element) datas.item(5);
                            String numbertText = number.getTextContent();
                            String titleText = title.getTextContent().trim();
                            String url = "";
                            if(title.getChildNodes().getLength() > 0){
                                NodeList link = title.getElementsByTagName("a");
                                url = ((Element)link.item(0)).getAttribute(TAG_HREF_LINK);
                            }
                            String authorText = author.getTextContent();
                            String dateText = date.getTextContent();
                            String readCunt = readCount.getTextContent();
                            url = TextUtils.isEmpty(url) ? this.url.toString() : config.townHostUrl + config.townPath + url;

                            TownNews townData = new TownNews(null, titleText, url, authorText, dateText);
                            townDataList.add(townData);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return townDataList;
    }

    @Override
    protected void onPostExecute(ArrayList<TownNews> townDatas) {
        super.onPostExecute(townDatas);
        onLoadTownDataListener.onLoad(townDatas);
    }
}
