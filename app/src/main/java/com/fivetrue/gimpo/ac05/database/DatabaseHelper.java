package com.fivetrue.gimpo.ac05.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fivetrue.gimpo.ac05.chatting.ChatMessageDatabase;
import com.fivetrue.gimpo.ac05.chatting.GalleryMessageDatabase;

/**
 * Created by kwonojin on 2016. 10. 14..
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DB_NAME = "ac05";
    private static final int VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ChatMessageDatabase.MESSAGE_DB_CREATE_QUERY);
        db.execSQL(GalleryMessageDatabase.MESSAGE_DB_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
