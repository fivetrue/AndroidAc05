package com.fivetrue.gimpo.ac05.service.notification;

import android.content.ContextWrapper;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ojin.kwon on 2016-02-25.
 */
public final class NotificationData implements Parcelable {

    public static final String ACTION_NOTIFICATION = NotificationData.class.getName() + ".notification";

    public int id = -1;
    public String title = null;
    public String message = null;
    public String targetClass = null;
    public String uri = null;
    public String imageUrl = null;
    public boolean hasLarge = false;

    public NotificationData(){}

    public NotificationData(int id, String title, String message, Class< ? extends ContextWrapper> targetCls, String uri){
        this(id, title, message, targetCls, uri, null, false);
    }

    public NotificationData(int id, String title, String message, Class< ? extends ContextWrapper> targetCls, String uri, String imageUrl){
        this(id, title, message, targetCls, uri, imageUrl, false);
    }

    public NotificationData(int id, String title, String message, Class< ? extends ContextWrapper> targetCls, String uri, String imageUrl, boolean hasLarge){
        this.id = id;
        this.title = title;
        this.message = message;
        this.targetClass = targetCls.getName();
        this.uri = uri;
        this.imageUrl = imageUrl;
        this.hasLarge = hasLarge;
    }

    public NotificationData(Parcel parcel){
        id = parcel.readInt();
        title = parcel.readString();
        message = parcel.readString();
        targetClass = parcel.readString();
        uri = parcel.readString();
        imageUrl = parcel.readString();
        hasLarge = parcel.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(targetClass);
        dest.writeString(uri);
        dest.writeString(imageUrl);
        dest.writeInt(hasLarge ? 1 : 0);
    }

    public static Creator<NotificationData> CREATOR = new Creator<NotificationData>() {
        @Override
        public NotificationData createFromParcel(Parcel source) {
            return new NotificationData(source);
        }

        @Override
        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };

}
