package com.fivetrue.gimpo.ac05.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fivetrue.gimpo.ac05.firebase.FirebaseData;
import com.fivetrue.gimpo.ac05.vo.IBaseItem;

import java.util.ArrayList;

/**
 * Created by kwonojin on 2016. 11. 4..
 */

public class ImageInfo extends FirebaseData implements IBaseItem, Parcelable{

    public String title;
    public String description;
    public ArrayList<String> images;

    public ImageInfo(){

    }

    protected ImageInfo(Parcel in) {
        title = in.readString();
        description = in.readString();
        images = in.createStringArrayList();
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel in) {
            return new ImageInfo(in);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };

    @Override
    public String getImageUrl() {
        return images.get(0);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContent() {
        return description;
    }

    @Override
    public String getSubContent() {
        return null;
    }

    @Override
    public long getTime() {
        return updateTime;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public boolean isShowingNew() {
        return getTime() + (ONE_DAY * 5) > System.currentTimeMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeStringList(images);
    }
}
