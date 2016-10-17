package com.fivetrue.gimpo.ac05.chatting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kwonojin on 2016. 10. 15..
 */

public class GalleryMessage implements Parcelable, MessageData{

    public String image;
    public String message;
    public String author;
    public String userImage;
    public long createTime;

    public GalleryMessage(){

    }

    public GalleryMessage(String image, String message, String author, String userImage, long createTime){
        this.image = image;
        this.message = message;
        this.author = author;
        this.userImage = userImage;
        this.createTime = createTime;
    }

    protected GalleryMessage(Parcel in) {
        image = in.readString();
        message = in.readString();
        author = in.readString();
        userImage = in.readString();
        createTime = in.readLong();
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(message);
        dest.writeString(author);
        dest.writeString(userImage);
        dest.writeLong(createTime);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getImageUrl() {
        return image;
    }

    @Override
    public String getUserImage() {
        return userImage;
    }

    @Override
    public String getUser() {
        return author;
    }

}
