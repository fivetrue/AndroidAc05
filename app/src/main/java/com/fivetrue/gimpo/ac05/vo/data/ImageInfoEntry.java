package com.fivetrue.gimpo.ac05.vo.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 7. 6..
 */
public class ImageInfoEntry implements Parcelable{

    private String title = null;
    private ArrayList<ImageInfo> imageInfos = new ArrayList<>();

    public ImageInfoEntry(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ImageInfo> getImageInfos() {
        return imageInfos;
    }

    public void setImageInfos(ArrayList<ImageInfo> imageInfos) {
        this.imageInfos = imageInfos;
    }

    @Override
    public String toString() {
        return "ImageInfoEntry{" +
                "title='" + title + '\'' +
                ", imageInfos=" + imageInfos +
                '}';
    }

    protected ImageInfoEntry(Parcel in) {
        title = in.readString();
        imageInfos = in.createTypedArrayList(ImageInfo.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(imageInfos);
    }

    public static final Creator<ImageInfoEntry> CREATOR = new Creator<ImageInfoEntry>() {
        @Override
        public ImageInfoEntry createFromParcel(Parcel in) {
            return new ImageInfoEntry(in);
        }

        @Override
        public ImageInfoEntry[] newArray(int size) {
            return new ImageInfoEntry[size];
        }
    };
}
