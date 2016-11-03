package com.fivetrue.gimpo.ac05.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;
import com.fivetrue.gimpo.ac05.firebase.model.TownNews;
import com.fivetrue.gimpo.ac05.firebase.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2016. 10. 14..
 */

public class TownNewsLocalDB extends BaseLocalDB{

    private static final String TAG = "TownNewsLocalDB";

    private static final String TABLE_NAME = "townNews";
    private static final String FIELD_KEY = "key";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_URL = "url";
    private static final String FIELD_AUTHOR = "author";
    private static final String FIELD_DATE= "date";
    private static final String FIELD_CREATE_TIME= "createTime";

    public TownNewsLocalDB(Context context){
        super(context);
    }

    public boolean existsTownNews(String url){
        Cursor c = limitQuery(TABLE_NAME, null, "url=?", new String[]{url}, 1);
        return c != null && c.getCount() > 0;
    }

    public boolean hasNewScrapContent(){
        Cursor c = limitQuery(TABLE_NAME, new String[]{FIELD_DATE}, null, null, 1);
        c.moveToFirst();
        return c != null && c.getCount() > 0 && c.getLong(0) + 1000 * 60 * 60 * 24 > System.currentTimeMillis();
    }

    public void putTownNews(String key, TownNews townNews){
        ContentValues values = new ContentValues();
        values.put(FIELD_KEY, key);
        values.put(FIELD_TITLE, townNews.title);
        values.put(FIELD_URL, townNews.url);
        values.put(FIELD_AUTHOR, townNews.author);
        values.put(FIELD_DATE, townNews.date);
        values.put(FIELD_URL, townNews.url);
        values.put(FIELD_CREATE_TIME, townNews.updateTime);
        insertValues(TABLE_NAME, values);
    }

    public long removeTownNews(String key){
        return remove(TABLE_NAME, "key=?", key);
    }

    public ArrayList<TownNews> getTownNews(boolean reverse){
        Cursor c = limitQuery(TABLE_NAME, null, null, null, FIELD_DATE + (reverse ? " DESC" : " ASC"), 1000);
        ArrayList<TownNews> townNewsArrayList = new ArrayList<>();
        if(c != null){
            try{
                c.moveToFirst();
                do{
                    String key = c.getString(c.getColumnIndex(FIELD_KEY));
                    String title = c.getString(c.getColumnIndex(FIELD_TITLE));
                    String url = c.getString(c.getColumnIndex(FIELD_URL));
                    String author = c.getString(c.getColumnIndex(FIELD_AUTHOR));
                    String date = c.getString(c.getColumnIndex(FIELD_DATE));
                    long createTime = c.getLong(c.getColumnIndex(FIELD_CREATE_TIME));
                    TownNews news = new TownNews(key, title, url, author, date);
                    news.updateTime = createTime;
                    townNewsArrayList.add(news);
                }while (c.moveToNext());
            }catch (Exception e){
                Log.w(TAG, "getChatMessages: ", e);
            }finally {
                if(c != null){
                    c.close();
                }

            }
        }
        return townNewsArrayList;
    }

    public static final String DB_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
            + FIELD_KEY + " TEXT PRIMARY KEY,"
            + FIELD_TITLE +" TEXT NOT NULL,"
            + FIELD_URL + " TEXT NOT NULL,"
            + FIELD_AUTHOR + " TEXT,"
            + FIELD_DATE + " TEXT,"
            + FIELD_CREATE_TIME + " INTEGER NOT NULL"
            +");";

    public static final String DB_DROP_QUERY = "DROP TABLE " + TABLE_NAME + ";";
}
