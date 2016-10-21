package com.fivetrue.gimpo.ac05.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.fivetrue.gimpo.ac05.chatting.GalleryMessage;
import com.fivetrue.gimpo.ac05.database.DatabaseHelper;
import com.fivetrue.gimpo.ac05.firebase.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2016. 10. 14..
 */

public class GalleryMessageDatabase extends BaseDatabase{

    private static final String TAG = "GalleryMessageDatabase";

    private static final String TABLE_NAME = "galleryMessage";

    private static final String FIELD_KEY = "key";
    private static final String FIELD_IMAGE = "image";
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_PATH = "path";
    private static final String FIELD_USER = "user";
    private static final String FIELD_CREATE_TIME = "createTime";
    private static final String FIELD_TYPE = "type";

    public GalleryMessageDatabase(Context context){
        super(context);
    }

    public boolean existsChatMessage(String key){
        Cursor c = limitQuery(TABLE_NAME, new String[]{FIELD_KEY}, "key=?", new String[]{key}, 1);
        return c != null && c.getCount() > 0;
    }

    public void putGalleryMessage(int type, String key, GalleryMessage msg){
        Log.d(TAG, "putGalleryMessage() called with: type = [" + type + "], key = [" + key + "], msg = [" + msg + "]");
        String user = new String(Base64.encode(getGson().toJson(msg.user).getBytes(), Base64.DEFAULT));
        ContentValues values = new ContentValues();
        values.put(FIELD_KEY, key);
        values.put(FIELD_MESSAGE, msg.message);
        values.put(FIELD_IMAGE, msg.image);
        values.put(FIELD_PATH, msg.path);
        values.put(FIELD_USER, user);
        values.put(FIELD_CREATE_TIME, msg.updateTime);
        values.put(FIELD_TYPE, type);
        insertValues(TABLE_NAME, values);
    }

    public long removeGalleryMessage(int type, GalleryMessage msg){
        return removeGalleryMessage(type, msg.key);
    }

    public long removeGalleryMessage(int type, String key){
        return remove(TABLE_NAME, "type=? and key=?", String.valueOf(type), key);
    }

    public List<GalleryMessage> getGalleryMessage(){
        Cursor c = limitQuery(TABLE_NAME, null, null, null, FIELD_CREATE_TIME + " DESC", 1000);
        ArrayList<GalleryMessage> galleryMessages = new ArrayList<>();
        if(c != null){
            try{
                c.moveToFirst();
                do{
                    String key = c.getString(c.getColumnIndex(FIELD_KEY));
                    String message = c.getString(c.getColumnIndex(FIELD_MESSAGE));
                    String image = c.getString(c.getColumnIndex(FIELD_IMAGE));
                    String path = c.getString(c.getColumnIndex(FIELD_PATH));
                    String userData = c.getString(c.getColumnIndex(FIELD_USER));
                    long createTime = c.getLong(c.getColumnIndex(FIELD_CREATE_TIME));
                    String u = new String(Base64.decode(userData.getBytes(), Base64.DEFAULT));
                    User user = getGson().fromJson(u, User.class);
                    GalleryMessage msg = new GalleryMessage(key, image, message, path, user);
                    msg.updateTime = createTime;
                    galleryMessages.add(msg);
                }while (c.moveToNext());
            }catch (Exception e){
                Log.w(TAG, "getChatMessages: ", e);
            }finally {
                if(c != null){
                    c.close();
                }
            }
        }
        return galleryMessages;
    }

    public static final String MESSAGE_DB_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
            + FIELD_KEY + " TEXT PRIMARY KEY,"
            + FIELD_MESSAGE +" TEXT NOT NULL,"
            + FIELD_IMAGE + " TEXT,"
            + FIELD_PATH + " TEXT,"
            + FIELD_USER + " TEXT NOT NULL,"
            + FIELD_CREATE_TIME + " INTEGER NOT NULL,"
            + FIELD_TYPE + " INTEGER NOT NULL"
            +");";

    public static final String MESSAGE_DB_DROP_QUERY = "DROP TABLE " + TABLE_NAME + ";";
}
