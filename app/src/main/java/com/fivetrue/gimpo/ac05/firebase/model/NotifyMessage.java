package com.fivetrue.gimpo.ac05.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fivetrue.gimpo.ac05.firebase.FirebaseData;
import com.fivetrue.gimpo.ac05.firebase.MessageData;

/**
 * Created by kwonojin on 2016. 10. 13..
 */

public class NotifyMessage extends  FirebaseData implements MessageData, Parcelable {

    public String title;
    public String message;
    public String deeplink;
    public String imageUrl;
    public User user;

    public NotifyMessage(){}

    public NotifyMessage(String title, String message
            , String deeplink, String imageUrl, User user){
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;
        this.deeplink = deeplink;
        this.user = user;
    }


    protected NotifyMessage(Parcel in) {
        title = in.readString();
        message = in.readString();
        deeplink = in.readString();
        imageUrl = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<NotifyMessage> CREATOR = new Creator<NotifyMessage>() {
        @Override
        public NotifyMessage createFromParcel(Parcel in) {
            return new NotifyMessage(in);
        }

        @Override
        public NotifyMessage[] newArray(int size) {
            return new NotifyMessage[size];
        }
    };

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public long getUpdateTime() {
        return updateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(deeplink);
        dest.writeString(imageUrl);
        dest.writeParcelable(user, flags);
    }
}
