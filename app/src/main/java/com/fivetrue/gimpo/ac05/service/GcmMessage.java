package com.fivetrue.gimpo.ac05.service;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.fivetrue.gimpo.ac05.Constants;
import com.google.gson.Gson;

/**
 * Created by kwonojin on 2016. 10. 21..
 */

public class GcmMessage {

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_URL = "url";
    private static final String KEY_DEEP_LINK = "deeplink";
    private static final String KEY_IMAGE_URL = "imageUrl";

    public int id;
    public String title;
    public String message;
    public String url;
    public String deeplink;
    public String imageUrl;

    public GcmMessage(){}

    public GcmMessage(Bundle b){
        id = b.getInt(KEY_ID);
        title = b.getString(KEY_TITLE);
        message = b.getString(KEY_MESSAGE);
        url = b.getString(KEY_URL);
        deeplink = b.getString(KEY_DEEP_LINK);
        imageUrl = b.getString(KEY_IMAGE_URL);
        if(title == null && message == null){
            String json = b.getString("data");
            GcmMessage message = new Gson().fromJson(json, GcmMessage.class);
            this.id = message.id;
            this.title = message.title;
            this.message = message.message;
            this.url = message.url;
            this.deeplink = message.deeplink;
            this.imageUrl = message.imageUrl;
        }
    }

    public GcmMessage(Intent intent){
        id = intent.getIntExtra(KEY_ID, 0);
        title = intent.getStringExtra(KEY_TITLE);
        message = intent.getStringExtra(KEY_MESSAGE);
        url = intent.getStringExtra(KEY_URL);
        imageUrl = intent.getStringExtra(KEY_IMAGE_URL);
        deeplink = intent.getDataString();
    }

    public Intent toIntent(){
        Intent intent = new Intent(Constants.ACTION_NOTIFICATION);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_MESSAGE, message);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_IMAGE_URL, imageUrl);
        if(deeplink != null){
            intent.setData(Uri.parse(deeplink));
        }
        return intent;
    }
}
