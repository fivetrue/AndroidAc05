package com.fivetrue.gimpo.ac05.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fivetrue.gimpo.ac05.firebase.FirebaseData;
import com.fivetrue.gimpo.ac05.firebase.MessageData;

/**
 * Created by kwonojin on 2016. 10. 13..
 */

public class ChatMessage extends FirebaseData implements Parcelable, MessageData {

    public String key;
    public String message;
    public String imageMessage;
    public User user;

    public ChatMessage(){}

    public ChatMessage(String key, String message, String imageMessage, User user){
        this.key = key;
        this.message = message;
        this.imageMessage = imageMessage;
        this.user = user;
    }


    protected ChatMessage(Parcel in) {
        key = in.readString();
        message = in.readString();
        imageMessage = in.readString();
        updateTime = in.readLong();
        user = in.readParcelable(User.class.getClassLoader());
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
        return TextUtils.isEmpty(imageMessage) ? user.profileImage : imageMessage;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(message);
        dest.writeString(imageMessage);
        dest.writeLong(updateTime);
        dest.writeParcelable(user, flags);
    }

}
