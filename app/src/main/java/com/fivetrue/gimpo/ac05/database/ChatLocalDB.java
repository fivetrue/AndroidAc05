package com.fivetrue.gimpo.ac05.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import com.fivetrue.gimpo.ac05.firebase.model.ChatMessage;
import com.fivetrue.gimpo.ac05.firebase.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2016. 10. 14..
 */

public class ChatLocalDB extends BaseLocalDB{

    private static final String TAG = "ChatMessageDatabase";

    private static final String TABLE_NAME = "chatMessage";
    private static final String FIELD_KEY = "key";
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_IMAGE_MESSAGE = "imageMessage";
    private static final String FIELD_USER = "user";
    private static final String FIELD_CREATE_TIME = "createTime";
    private static final String FIELD_TYPE = "type";


    public ChatLocalDB(Context context){
        super(context);
    }

    public boolean existsChatMessage(int type, String key){
        Cursor c = limitQuery(TABLE_NAME, null, "key=? and type=?", new String[]{key, String.valueOf(type)}, 1);
        return c != null && c.getCount() > 0;
    }

    public boolean hasNewChatMessage(int type){
        Cursor c = limitQuery(TABLE_NAME, new String[]{FIELD_CREATE_TIME}, "type=?", new String[]{String.valueOf(type)}, 1);
        c.moveToFirst();
        return c != null && c.getCount() > 0 && c.getLong(0) + 1000 * 60 * 60 * 24 > System.currentTimeMillis();
    }

    public void putChatMessage(int type, String key, ChatMessage msg){
        Log.d(TAG, "putChatMessage() called with: type = [" + type + "], key = [" + key + "], msg = [" + msg + "]");
        String user = new String(Base64.encode(getGson().toJson(msg.user).getBytes(), Base64.DEFAULT));
        ContentValues values = new ContentValues();
        values.put(FIELD_KEY, key);
        values.put(FIELD_MESSAGE, msg.message);
        values.put(FIELD_IMAGE_MESSAGE, msg.imageMessage);
        values.put(FIELD_USER, user);
        values.put(FIELD_CREATE_TIME, msg.updateTime);
        values.put(FIELD_TYPE, type);
        insertValues(TABLE_NAME, values);
    }

    public int removeChatMessage(int type, ChatMessage msg){
        return removeChatMessage(type, msg);
    }

    public long removeChatMessage(int type, String key){
        return remove(TABLE_NAME, "type=? and key=?", String.valueOf(type), key);
    }

    public List<ChatMessage> getChatMessages(int type){
        return getChatMessages(type, false);
    }

    public List<ChatMessage> getChatMessages(int type, boolean reverse){
        Cursor c = limitQuery(TABLE_NAME, null, "type=?", new String[]{String.valueOf(type)}, FIELD_CREATE_TIME + (reverse ? " DESC" : " ASC"), 1000);
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        if(c != null){
            try{
                c.moveToFirst();
                do{
                    String key = c.getString(c.getColumnIndex(FIELD_KEY));
                    String message = c.getString(c.getColumnIndex(FIELD_MESSAGE));
                    String imageMessage = c.getString(c.getColumnIndex(FIELD_IMAGE_MESSAGE));
                    String userData = c.getString(c.getColumnIndex(FIELD_USER));
                    long createTime = c.getLong(c.getColumnIndex(FIELD_CREATE_TIME));
                    String d = new String(Base64.decode(userData.getBytes(), Base64.DEFAULT));
                    User user = getGson().fromJson(d, User.class);
                    ChatMessage msg = new ChatMessage(key, message, imageMessage, user);
                    msg.updateTime = createTime;
                    chatMessages.add(msg);
                }while (c.moveToNext());
            }catch (Exception e){
                Log.w(TAG, "getChatMessages: ", e);
            }finally {
                if(c != null){
                    c.close();
                }

            }
        }
        return chatMessages;
    }

    public static final String DB_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
            + FIELD_KEY + " TEXT PRIMARY KEY,"
            + FIELD_MESSAGE +" TEXT NOT NULL,"
            + FIELD_IMAGE_MESSAGE + " TEXT,"
            + FIELD_USER + " TEXT NOT NULL,"
            + FIELD_CREATE_TIME + " INTEGER NOT NULL,"
            + FIELD_TYPE + " INTEGER NOT NULL"
            +");";

    public static final String DB_DROP_QUERY = "DROP TABLE " + TABLE_NAME + ";";
}
