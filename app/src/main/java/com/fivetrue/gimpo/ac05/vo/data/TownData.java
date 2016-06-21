package com.fivetrue.gimpo.ac05.vo.data;


import android.os.Parcel;
import android.os.Parcelable;

public class TownData implements Parcelable, IPageData{
	
	private int postId = 0;
	private String title = null;
	private String url = null;
	
	private String titleColor = null;
	private String titleBgColor = null;
	
	private String contentColor = null;
	private String contentBgColor = null;
	
	private String author = null;
	private String date = null;
	
	private String content = null;

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "TownData [postId=" + postId + ", title=" + title + ", url=" + url + ", titleColor=" + titleColor
				+ ", titleBgColor=" + titleBgColor + ", contentColor=" + contentColor + ", contentBgColor="
				+ contentBgColor + ", content=" + content + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(postId);
		dest.writeString(title);
		dest.writeString(url);
		dest.writeString(titleColor);
		dest.writeString(titleBgColor);
		dest.writeString(contentColor);
		dest.writeString(contentBgColor);
		dest.writeString(author);
		dest.writeString(date);
		dest.writeString(content);
	}

	protected TownData(Parcel in) {
		postId = in.readInt();
		title = in.readString();
		url = in.readString();
		titleColor = in.readString();
		titleBgColor = in.readString();
		contentColor = in.readString();
		contentBgColor = in.readString();
		author = in.readString();
		date = in.readString();
		content = in.readString();
	}

	public static final Creator<TownData> CREATOR = new Creator<TownData>() {
		@Override
		public TownData createFromParcel(Parcel in) {
			return new TownData(in);
		}

		@Override
		public TownData[] newArray(int size) {
			return new TownData[size];
		}
	};
}
