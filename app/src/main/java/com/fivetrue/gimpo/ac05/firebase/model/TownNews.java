package com.fivetrue.gimpo.ac05.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.firebase.FirebaseData;
import com.fivetrue.gimpo.ac05.firebase.MessageData;
import com.fivetrue.gimpo.ac05.vo.IBaseItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kwonojin on 2016. 11. 1..
 */

public class TownNews extends FirebaseData implements Parcelable, IBaseItem , MessageData{

    private static final String TAG = "TownNews";

    public String key;
    public String title;
    public String url;
    public String author;
    public String date;

    public TownNews(){

    }

    public TownNews(String key, String title, String url, String author, String date){
        this.key = key;
        this.title = title;
        this.url = url;
        this.author = author;
        this.date = date;
    }

    protected TownNews(Parcel in) {
        key = in.readString();
        title = in.readString();
        url = in.readString();
        author = in.readString();
        date = in.readString();
        updateTime = in.readLong();
    }

    public static final Creator<TownNews> CREATOR = new Creator<TownNews>() {
        @Override
        public TownNews createFromParcel(Parcel in) {
            return new TownNews(in);
        }

        @Override
        public TownNews[] newArray(int size) {
            return new TownNews[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(author);
        dest.writeString(date);
        dest.writeLong(updateTime);
    }

    @Override
    public String getMessage() {
        return title;
    }

    @Override
    public String getImageUrl() {
        return Constants.DEFAULT_TOWN_BI_IMAGE_URL;
    }

    @Override
    public User getUser() {
        return null;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContent() {
        return date;
    }

    @Override
    public String getSubContent() {
        return null;
    }

    @Override
    public long getTime() {
        long milliseconds = 0;
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).parse(this.date);
            milliseconds= date.getTime();
        } catch (ParseException e) {
            Log.w(TAG, "getTime: ", e);
        }
        return milliseconds;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public boolean isShowingNew() {
        return getTime() + (ONE_DAY * 5) > System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof TownNews){
            return this.url.equals(((TownNews) obj).url);
        }
        return super.equals(obj);
    }
}
