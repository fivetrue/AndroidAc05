package com.fivetrue.gimpo.ac05.vo.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.fivetrue.gimpo.ac05.vo.IPageData;
import com.fivetrue.gimpo.ac05.vo.rss.Feed;

public class PageData implements Parcelable, IPageData{


    public PageData(){

    }

    private int pageId = 0;

    private String pageTitle = null;

    private String pageUrl = null;

    private String titleColor = null;
    private String titleBgColor = null;

    private String contentColor = null;
    private String contentBgColor = null;

    private String contentDescription = null;

    private String pageType = "None";

    private Feed feed = null;

    @Override
    public String getTitle() {
        return pageTitle;
    }

    @Override
    public String getContent() {
        return contentDescription;
    }

    @Override
    public String getUrl() {
        return pageUrl;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public String getTitleBgColor() {
        return titleBgColor;
    }

    public void setTitleBgColor(String titleBgColor) {
        this.titleBgColor = titleBgColor;
    }

    public String getContentColor() {
        return contentColor;
    }

    public void setContentColor(String contentColor) {
        this.contentColor = contentColor;
    }

    public String getContentBgColor() {
        return contentBgColor;
    }

    public void setContentBgColor(String contentBgColor) {
        this.contentBgColor = contentBgColor;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    @Override
    public String toString() {
        return "PageData [pageId=" + pageId + ", pageTitle=" + pageTitle + ", pageUrl=" + pageUrl + ", titleColor="
                + titleColor + ", titleBgColor=" + titleBgColor + ", contentColor=" + contentColor + ", contentBgColor="
                + contentBgColor + ", contentDescription=" + contentDescription + ", pageType=" + pageType
                + "]";
    }

    protected PageData(Parcel in) {
        pageId = in.readInt();
        pageTitle = in.readString();
        pageUrl = in.readString();
        titleColor = in.readString();
        titleBgColor = in.readString();
        contentColor = in.readString();
        contentBgColor = in.readString();
        contentDescription = in.readString();
        pageType = in.readString();
    }

    public static final Creator<PageData> CREATOR = new Creator<PageData>() {
        @Override
        public PageData createFromParcel(Parcel in) {
            return new PageData(in);
        }

        @Override
        public PageData[] newArray(int size) {
            return new PageData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pageId);
        dest.writeString(pageTitle);
        dest.writeString(pageUrl);
        dest.writeString(titleColor);
        dest.writeString(titleBgColor);
        dest.writeString(contentColor);
        dest.writeString(contentBgColor);
        dest.writeString(contentDescription);
        dest.writeString(pageType);
    }
}
