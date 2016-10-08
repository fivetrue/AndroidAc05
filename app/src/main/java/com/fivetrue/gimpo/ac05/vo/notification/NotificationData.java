package com.fivetrue.gimpo.ac05.vo.notification;

import android.content.ContextWrapper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.vo.IBaseItem;

/**
 * Created by ojin.kwon on 2016-02-25.
 */
public final class NotificationData implements Parcelable, IBaseItem {

    private String multicast_id = null;
    private int id = -1;
    private String title = null;
    private String message = null;
    private String targetClass = null;
    private String uri = null;
    private String imageUrl = null;

    private long createTime = 0;

    private String authorNickname = null;
    private String authorEmail = null;


    public NotificationData(){}

    public NotificationData(int id, String title, String message, Class< ? extends ContextWrapper> targetCls, String uri){
        this(id, title, message, targetCls, uri, null);
    }

    public NotificationData(int id, String title, String message, Class< ? extends ContextWrapper> targetCls, String uri, String imageUrl){
        this.id = id;
        this.title = title;
        this.message = message;
        this.targetClass = targetCls.getName();
        this.uri = uri;
        this.imageUrl = imageUrl;
    }

    public String getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(String multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getContent() {
        return message;
    }

    @Override
    public String getSubContent() {
        return null;
    }

    @Override
    public long getTime() {
        return createTime;
    }

    @Override
    public String getUrl() {
        return uri;
    }

    @Override
    public boolean isShowingNew() {
        return System.currentTimeMillis() - ONE_DAY * 3 < createTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getImageUrl() {
        return TextUtils.isEmpty(imageUrl) ? Constants.DEFAULT_DEFAULT_IMAGE_URL : imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    protected NotificationData(Parcel in) {
        multicast_id = in.readString();
        id = in.readInt();
        title = in.readString();
        message = in.readString();
        targetClass = in.readString();
        uri = in.readString();
        imageUrl = in.readString();
        createTime = in.readLong();
        authorNickname = in.readString();
        authorEmail = in.readString();
    }

    public static final Creator<NotificationData> CREATOR = new Creator<NotificationData>() {
        @Override
        public NotificationData createFromParcel(Parcel in) {
            return new NotificationData(in);
        }

        @Override
        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(multicast_id);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(targetClass);
        dest.writeString(uri);
        dest.writeString(imageUrl);
        dest.writeLong(createTime);
        dest.writeString(authorNickname);
        dest.writeString(authorEmail);
    }
}
