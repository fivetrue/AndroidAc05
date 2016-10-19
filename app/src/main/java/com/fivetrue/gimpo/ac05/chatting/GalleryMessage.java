package com.fivetrue.gimpo.ac05.chatting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by kwonojin on 2016. 10. 15..
 */

public class GalleryMessage implements Parcelable, MessageData{

    public String key;
    public String image;
    public String message;
    public String author;
    public String userImage;
    public String authorId;
    public long createTime;

    public GalleryMessage(){

    }

    public GalleryMessage(String key, String image, String message, String author, String authorId, String userImage, long createTime){
        this.key = key;
        this.image = image;
        this.message = message;
        this.author = author;
        this.authorId = authorId;
        this.userImage = userImage;
        this.createTime = createTime;
    }

    protected GalleryMessage(Parcel in) {
        key = in.readString();
        image = in.readString();
        message = in.readString();
        author = in.readString();
        userImage = in.readString();
        authorId = in.readString();
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

    @Override
    public String getName() {
        String name = "";
        if(author != null){
            name = author.substring(0, author.indexOf("@"));
        }
        return name;
    }

    @Override
    public String getUserId() {
        return authorId;
    }

    @Override
    public HashMap<String, Object> getValues() {
        HashMap<String, Object> v = new HashMap<>();
        v.put("image", image);
        v.put("message", message);
        v.put("author", author);
        v.put("authorId", authorId);
        v.put("userImage", userImage);
        v.put("createTime", ServerValue.TIMESTAMP);
        return v;
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
        dest.writeString(author);
        dest.writeString(userImage);
        dest.writeString(authorId);
        dest.writeLong(createTime);
    }
}
