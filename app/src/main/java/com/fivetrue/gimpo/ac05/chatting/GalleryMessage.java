package com.fivetrue.gimpo.ac05.chatting;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fivetrue.gimpo.ac05.firebase.FirebaseData;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by kwonojin on 2016. 10. 15..
 */

public class GalleryMessage extends FirebaseData implements Parcelable, MessageData{

    public String key;
    public String image;
    public String message;
    public String path;
    public User user;

    public GalleryMessage(){

    }

    public GalleryMessage(String key, String image, String message, String path, User user){
        this.key = key;
        this.image = image;
        this.message = message;
        this.path = path;
        this.user = user;
    }


    protected GalleryMessage(Parcel in) {
        key = in.readString();
        image = in.readString();
        message = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        updateTime = in.readLong();
    }

    public static final Creator<GalleryMessage> CREATOR = new Creator<GalleryMessage>() {
        @Override
        public GalleryMessage createFromParcel(Parcel in) {
            return new GalleryMessage(in);
        }

        @Override
        public GalleryMessage[] newArray(int size) {
            return new GalleryMessage[size];
        }
    };

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getImageUrl() {
        return image;
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
        dest.writeString(image);
        dest.writeString(message);
        dest.writeParcelable(user, flags);
        dest.writeLong(updateTime);
    }
}
