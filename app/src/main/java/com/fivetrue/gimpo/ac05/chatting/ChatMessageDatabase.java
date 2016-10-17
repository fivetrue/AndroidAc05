package com.fivetrue.gimpo.ac05.chatting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fivetrue.gimpo.ac05.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2016. 10. 14..
 */

public class ChatMessageDatabase {

    private static final String TAG = "ChatMessageDatabase";

    private static final String TABLE_NAME = "chatMessage";
    private static final String FIELD_KEY = "key";
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_IMAGE_MESSAGE = "imageMessage";
    private static final String FIELD_SENDER = "sender";
    private static final String FIELD_USER_IMAGE = "userImage";
    private static final String FIELD_CREATE_TIME = "createTime";
    private static final String FIELD_TYPE = "type";

    private Context mContext;

    private DatabaseHelper mDatabaseHelper;

    public ChatMessageDatabase(Context context){
        mContext = context;
        mDatabaseHelper = new DatabaseHelper(context);
    }

    public boolean existsChatMessage(int type, String key){
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        db.beginTransaction();
        Cursor c = db.query(TABLE_NAME, new String[]{FIELD_KEY}, "key=? and type=?", new String[]{key, String.valueOf(type)}, null, null, null, "1");
        db.endTransaction();
        return c != null && c.getCount() > 0;
    }

    public void putChatMessage(int type, String key, ChatMessage msg){
        Log.d(TAG, "putChatMessage() called with: type = [" + type + "], key = [" + key + "], msg = [" + msg + "]");
        ContentValues values = new ContentValues();
        values.put(FIELD_KEY, key);
        values.put(FIELD_MESSAGE, msg.message);
        values.put(FIELD_USER_IMAGE, msg.userImage);
        values.put(FIELD_IMAGE_MESSAGE, msg.imageMessage);
        values.put(FIELD_SENDER, msg.sender);
        values.put(FIELD_CREATE_TIME, msg.createTime);
        values.put(FIELD_TYPE, type);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
    }

    public List<ChatMessage> getChatMessages(int type){
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        db.beginTransaction();
        Cursor c = db.query(TABLE_NAME, null, "type=" + type, null, null, null, FIELD_CREATE_TIME + " ASC", null);
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        if(c != null){
            try{
                c.moveToFirst();
                do{
                    String message = c.getString(c.getColumnIndex(FIELD_MESSAGE));
                    String imageMessage = c.getString(c.getColumnIndex(FIELD_IMAGE_MESSAGE));
                    String sender = c.getString(c.getColumnIndex(FIELD_SENDER));
                    String userImage = c.getString(c.getColumnIndex(FIELD_USER_IMAGE));
                    long createTime = c.getLong(c.getColumnIndex(FIELD_CREATE_TIME));
                    ChatMessage msg = new ChatMessage(message, imageMessage, sender, userImage, createTime);
                    chatMessages.add(msg);
                }while (c.moveToNext());
            }catch (Exception e){
                Log.w(TAG, "getChatMessages: ", e);
            }finally {
                if(c != null){
                    c.close();
                }
                db.endTransaction();

            }
        }
        return chatMessages;
    }

    public static final String MESSAGE_DB_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
            + FIELD_KEY + " TEXT PRIMARY KEY,"
            + FIELD_MESSAGE +" TEXT NOT NULL,"
            + FIELD_IMAGE_MESSAGE + " TEXT,"
            + FIELD_SENDER + " TEXT NOT NULL,"
            + FIELD_USER_IMAGE + " TEXT NOT NULL,"
            + FIELD_CREATE_TIME + " INTEGER NOT NULL,"
            + FIELD_TYPE + " INTEGER NOT NULL"
            +");";
}
