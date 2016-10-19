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
    private static final String FIELD_SENDER_ID = "senderId";
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

    public boolean hasNewChatMessage(int type){
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        db.beginTransaction();
        Cursor c = db.query(TABLE_NAME, new String[]{FIELD_CREATE_TIME}, "type=?", new String[]{String.valueOf(type)}, null, null, null, "1");
        db.endTransaction();
        c.moveToFirst();
        return c != null && c.getCount() > 0 && c.getLong(0) + 1000 * 60 * 60 * 24 > System.currentTimeMillis();
    }

    public void putChatMessage(int type, String key, ChatMessage msg){
        Log.d(TAG, "putChatMessage() called with: type = [" + type + "], key = [" + key + "], msg = [" + msg + "]");
        ContentValues values = new ContentValues();
        values.put(FIELD_KEY, key);
        values.put(FIELD_MESSAGE, msg.message);
        values.put(FIELD_USER_IMAGE, msg.userImage);
        values.put(FIELD_IMAGE_MESSAGE, msg.imageMessage);
        values.put(FIELD_SENDER, msg.sender);
        values.put(FIELD_SENDER_ID, msg.senderId);
        values.put(FIELD_CREATE_TIME, msg.createTime);
        values.put(FIELD_TYPE, type);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
    }

    public int removeChatMessage(int type, ChatMessage msg){
        return removeChatMessage(type, msg);
    }

    public int removeChatMessage(int type, String key){
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        return db.delete(TABLE_NAME, "type=? and key=?", new String[]{type + "", key});
    }

    public List<ChatMessage> getChatMessages(int type){
        return getChatMessages(type, false);
    }

    public List<ChatMessage> getChatMessages(int type, boolean reverse){
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        db.beginTransaction();
        Cursor c = db.query(TABLE_NAME, null, "type=" + type, null, null, null, FIELD_CREATE_TIME + (reverse ? " DESC" : " ASC"), null);
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        if(c != null){
            try{
                c.moveToFirst();
                do{
                    String key = c.getString(c.getColumnIndex(FIELD_KEY));
                    String message = c.getString(c.getColumnIndex(FIELD_MESSAGE));
                    String imageMessage = c.getString(c.getColumnIndex(FIELD_IMAGE_MESSAGE));
                    String sender = c.getString(c.getColumnIndex(FIELD_SENDER));
                    String senderId = c.getString(c.getColumnIndex(FIELD_SENDER_ID));
                    String userImage = c.getString(c.getColumnIndex(FIELD_USER_IMAGE));
                    long createTime = c.getLong(c.getColumnIndex(FIELD_CREATE_TIME));
                    ChatMessage msg = new ChatMessage(key, message, imageMessage, sender, senderId, userImage, createTime);
                    msg.createTime = createTime;
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
            + FIELD_SENDER_ID + " TEXT NOT NULL,"
            + FIELD_USER_IMAGE + " TEXT NOT NULL,"
            + FIELD_CREATE_TIME + " INTEGER NOT NULL,"
            + FIELD_TYPE + " INTEGER NOT NULL"
            +");";

    public static final String MESSAGE_DB_DROP_QUERY = "DROP TABLE " + TABLE_NAME + ";";
}
