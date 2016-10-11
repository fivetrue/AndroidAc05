package com.fivetrue.gimpo.ac05.vo.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.vo.IBaseItem;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 7. 6..
 */
public class ImageInfoEntry implements Parcelable , IBaseItem{

    private String title = null;
    private String content = null;
    private ArrayList<ImageInfo> imageInfos = null;
    private long updateDate = 0;
    private int totalCount = 0;

    protected ImageInfoEntry(Parcel in) {
        title = in.readString();
        content = in.readString();
        updateDate = in.readLong();
        imageInfos = in.createTypedArrayList(ImageInfo.CREATOR);
        totalCount = in.readInt();
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

    @Override
    public String getImageUrl() {
        return imageInfos != null && imageInfos.size() > 0 ? imageInfos.get(0).getImageUrl() : Constants.DEFAULT_DEFAULT_IMAGE_URL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String getSubContent() {
        return totalCount + " 개의 사진";
    }

    @Override
    public long getTime() {
        return updateDate;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public boolean isShowingNew() {
        return (System.currentTimeMillis() - (ONE_DAY * 3)) < getTime();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<ImageInfo> getImageInfos() {
        return imageInfos;
    }

    public void setImageInfos(ArrayList<ImageInfo> imageInfos) {
        this.imageInfos = imageInfos;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(updateDate);
        dest.writeTypedList(imageInfos);
        dest.writeInt(totalCount);
    }
}
