package com.fivetrue.gimpo.ac05.chatting;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class BadUserReportMessage implements MessageData {

    public final String userId;
    public final String name;
    public final String imageUrl;
    public final String message;

    public BadUserReportMessage(String userId, String name, String imageUrl, String message) {
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public String getUserImage() {
        return null;
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public HashMap<String, Object> getValues() {
        HashMap<String, Object> v = new HashMap<>();
        v.put("userId", userId);
        v.put("name", name);
        v.put("imageUrl", imageUrl);
        v.put("message", message);
        v.put("createTime", ServerValue.TIMESTAMP);
        return v;
    }
}
