package com.fivetrue.gimpo.ac05.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fivetrue.gimpo.ac05.firebase.FirebaseData;
import com.fivetrue.gimpo.ac05.firebase.MessageData;
import com.fivetrue.gimpo.ac05.vo.IBaseItem;

/**
 * Created by kwonojin on 2016. 10. 23..
 */

public class ScrapContent extends FirebaseData implements MessageData, IBaseItem, Parcelable{

    public String key;
    public String title;
    public String pageUrl;
    public String pagePath;
    public String imageUrl;
    public String url;
    public User user;

    public ScrapContent(){};

    public ScrapContent(String key, String title, String pageUrl, String pagePath, String imageUrl, String url, User user){
        this.key = key;
        this.title = title;
        this.pageUrl = pageUrl;
        this.pagePath = pagePath;
        this.imageUrl = imageUrl;
        this.url = url;
        this.user = user;
    }

    protected ScrapContent(Parcel in) {
        key = in.readString();
        title = in.readString();
        pageUrl = in.readString();
        pagePath = in.readString();
        imageUrl = in.readString();
        url = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<ScrapContent> CREATOR = new Creator<ScrapContent>() {
        @Override
        public ScrapContent createFromParcel(Parcel in) {
            return new ScrapContent(in);
        }

        @Override
        public ScrapContent[] newArray(int size) {
            return new ScrapContent[size];
        }
    };

    @Override
    public String getMessage() {
        return title;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContent() {
        return null;
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
        return url;
    }

    @Override
    public boolean isShowingNew() {
        return (System.currentTimeMillis() - (ONE_DAY * 2)) < getTime();
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
        dest.writeString(title);
        dest.writeString(pageUrl);
        dest.writeString(pagePath);
        dest.writeString(imageUrl);
        dest.writeString(url);
        dest.writeParcelable(user, flags);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof ScrapContent){
            return this.pageUrl.equals(((ScrapContent) obj).pageUrl);
        }
        return super.equals(obj);
    }
}
