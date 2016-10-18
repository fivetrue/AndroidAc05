package com.fivetrue.gimpo.ac05.chatting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by kwonojin on 2016. 10. 13..
 */

public class ChatMessage implements Parcelable, MessageData{

    public String message;
    public String imageMessage;
    public String sender;
    public String userImage;
    public long createTime;

    public ChatMessage(){}

    public ChatMessage(String message, String imageMessage, String sender, String userImage, long createTime){
        this.message = message;
        this.imageMessage = imageMessage;
        this.sender = sender;
        this.userImage = userImage;
        this.createTime = createTime;
    }

    protected ChatMessage(Parcel in) {
        message = in.readString();
        imageMessage = in.readString();
        sender = in.readString();
        userImage = in.readString();
        createTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(imageMessage);
        dest.writeString(sender);
        dest.writeString(userImage);
        dest.writeLong(createTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getImageUrl() {
        return imageMessage;
    }

    @Override
    public String getUserImage() {
        return userImage;
    }

    @Override
    public String getUser() {
        return sender;
    }

    @Override
    public HashMap<String, Object> getValues(){
        HashMap<String, Object> v = new HashMap<>();
        v.put("message", message);
        v.put("imageMessage", imageMessage);
        v.put("sender", sender);
        v.put("userImage", userImage);
        v.put("createTime", ServerValue.TIMESTAMP);
        return v;
    }

}
