package com.fivetrue.gimpo.ac05.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

/**
 * Created by kwonojin on 2016. 10. 20..
 */

public class BaseLocalDB {

    private static final String TAG = "BaseDatabase";

    private Context mContext;
    private DatabaseHelper mDatabaseHelper;

    private Gson mGson;


    public BaseLocalDB(Context context){
        mContext = context;
        mDatabaseHelper = new DatabaseHelper(context);
        mGson = new Gson();
    }

    protected Cursor limitQuery(String table, String[] fields, String selection, String[] selectionArg, String orderBy, int limit){
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        db.beginTransaction();
        Cursor c = db.query(table, fields, selection, selectionArg, null, null, orderBy, String.valueOf(limit));
        db.endTransaction();
        return c;
    }

    protected Cursor limitQuery(String table, String[] fields, String selection, String[] selectionArg, int limit){
        return limitQuery(table, fields, selection, selectionArg, null, limit);
    }

    protected long insertValues(String table, ContentValues values){
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        return db.insert(table, null, values);
    }

    protected long remove(String table, String selection, String... selectionArg){
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        return db.delete(table, selection, selectionArg);
    }

    protected Gson getGson(){
        return mGson;
    }
}
