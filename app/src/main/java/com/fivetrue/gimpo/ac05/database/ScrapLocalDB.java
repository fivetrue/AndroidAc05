package com.fivetrue.gimpo.ac05.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;
import com.fivetrue.gimpo.ac05.firebase.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2016. 10. 14..
 */

public class ScrapLocalDB extends BaseLocalDB{

    private static final String TAG = "ScrapLocalDB";

    private static final String TABLE_NAME = "scarp";
    private static final String FIELD_KEY = "key";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_PAGE_URL = "pageUrl";
    private static final String FIELD_PAGE_PATH = "pagePath";
    private static final String FIELD_IMAGE_URL= "imageUrl";
    private static final String FIELD_URL = "url";
    private static final String FIELD_USER = "user";
    private static final String FIELD_CREATE_TIME = "createTime";


    public ScrapLocalDB(Context context){
        super(context);
    }

    public boolean existsScrapContent(String url){
        Cursor c = limitQuery(TABLE_NAME, null, "url=?", new String[]{url}, 1);
        return c != null && c.getCount() > 0;
    }

    public boolean hasNewScrapContent(){
        Cursor c = limitQuery(TABLE_NAME, new String[]{FIELD_CREATE_TIME}, null, null, 1);
        c.moveToFirst();
        return c != null && c.getCount() > 0 && c.getLong(0) + 1000 * 60 * 60 * 24 > System.currentTimeMillis();
    }

    public void putScrap(String key, ScrapContent scrapContent){
        Log.d(TAG, "putScrap() called with: key = [" + key + "], scrapContent = [" + scrapContent + "]");
        String user = new String(Base64.encode(getGson().toJson(scrapContent.user).getBytes(), Base64.DEFAULT));
        ContentValues values = new ContentValues();
        values.put(FIELD_KEY, key);
        values.put(FIELD_TITLE, scrapContent.title);
        values.put(FIELD_PAGE_URL, scrapContent.pageUrl);
        values.put(FIELD_PAGE_PATH, scrapContent.pagePath);
        values.put(FIELD_IMAGE_URL, scrapContent.imageUrl);
        values.put(FIELD_URL, scrapContent.url);
        values.put(FIELD_USER, user);
        values.put(FIELD_CREATE_TIME, scrapContent.updateTime);
        insertValues(TABLE_NAME, values);
    }

    public int removeScrapContent(ScrapContent content){
        return removeScrapContent(content);
    }

    public long removeScrapContent(String url){
        return remove(TABLE_NAME, "url=?", url);
    }

    public List<ScrapContent> getScrapContent(boolean reverse){
        Cursor c = limitQuery(TABLE_NAME, null, null, null, FIELD_CREATE_TIME + (reverse ? " DESC" : " ASC"), 1000);
        ArrayList<ScrapContent> scrapContents = new ArrayList<>();
        if(c != null){
            try{
                c.moveToFirst();
                do{
                    String key = c.getString(c.getColumnIndex(FIELD_KEY));
                    String title = c.getString(c.getColumnIndex(FIELD_TITLE));
                    String pageUrl = c.getString(c.getColumnIndex(FIELD_PAGE_URL));
                    String pagePath = c.getString(c.getColumnIndex(FIELD_PAGE_PATH));
                    String imageUrl = c.getString(c.getColumnIndex(FIELD_IMAGE_URL));
                    String url = c.getString(c.getColumnIndex(FIELD_URL));
                    String userData = c.getString(c.getColumnIndex(FIELD_USER));
                    long createTime = c.getLong(c.getColumnIndex(FIELD_CREATE_TIME));
                    String d = new String(Base64.decode(userData.getBytes(), Base64.DEFAULT));
                    User user = getGson().fromJson(d, User.class);

                    ScrapContent content = new ScrapContent(key, title, pageUrl, pagePath, imageUrl, url, user);
                    content.updateTime = createTime;
                    scrapContents.add(content);
                }while (c.moveToNext());
            }catch (Exception e){
                Log.w(TAG, "getChatMessages: ", e);
            }finally {
                if(c != null){
                    c.close();
                }

            }
        }
        return scrapContents;
    }

    public static final String DB_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
            + FIELD_KEY + " TEXT PRIMARY KEY,"
            + FIELD_URL + " TEXT NOT NULL,"
            + FIELD_TITLE +" TEXT NOT NULL,"
            + FIELD_PAGE_URL + " TEXT,"
            + FIELD_PAGE_PATH + " TEXT,"
            + FIELD_IMAGE_URL + " TEXT,"
            + FIELD_USER + " TEXT NOT NULL,"
            + FIELD_CREATE_TIME + " INTEGER NOT NULL"
            +");";

    public static final String DB_DROP_QUERY = "DROP TABLE " + TABLE_NAME + ";";
}
