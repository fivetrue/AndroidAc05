package com.fivetrue.gimpo.ac05.vo.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class PageData implements Parcelable{

    public enum PageType{
        None,
        Town,
        Journal,
        News,
    }

    private String pageTitle = null;
    private String pageUrl = null;

    private String pageContent = null;
    private String pageAuthor = null;
    private String pageType = "None";
    private String pageDate = null;

    public String getPageTitle() {
        return new String(Base64.decode(pageTitle.getBytes(), Base64.DEFAULT));
    }
    public void setPageTitle(String pageTitle) {
        this.pageTitle = Base64.encodeToString(pageTitle.getBytes(), Base64.DEFAULT);;
    }
    public String getPageUrl() {
        return pageUrl;
    }
    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPageContent() {
        return new String(Base64.decode(pageContent.getBytes(), Base64.DEFAULT));
    }
    public void setPageContent(String pageContent) {
        this.pageTitle = Base64.encodeToString(pageTitle.getBytes(), Base64.DEFAULT);;
    }

    public String getPageAuthor() {
        return pageAuthor;
    }
    public void setPageAuthor(String pageAuthor) {
        this.pageAuthor = pageAuthor;
    }

    public String getPageType() {
        return pageType;
    }
    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public PageType getType(){
        return PageType.valueOf(pageType);
    }

    public String getPageDate() {
        return pageDate;
    }
    public void setPageDate(String pageDate) {
        this.pageDate = pageDate;
    }

    @Override
    public String toString() {
        return "PageData [pageTitle=" + pageTitle + ", pageUrl=" + pageUrl + ", pageContent=" + pageContent
                + ", pageAuthor=" + pageAuthor + ", pageType=" + pageType + ", pageDate=" + pageDate + "]";
    }

    protected PageData(Parcel in) {
        pageTitle = in.readString();
        pageUrl = in.readString();
        pageContent = in.readString();
        pageAuthor = in.readString();
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
        dest.writeString(pageTitle);
        dest.writeString(pageUrl);
        dest.writeString(pageContent);
        dest.writeString(pageAuthor);
        dest.writeString(pageType);
    }

}
