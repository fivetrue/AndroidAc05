package com.fivetrue.gimpo.ac05.rss;

import android.os.Parcel;
import android.os.Parcelable;

import com.fivetrue.gimpo.ac05.vo.data.IPageData;

public class FeedMessage implements Parcelable{

	String title;
	String description;
	String link;
	String author;
	String guid;
	String pubDate = null;

	public FeedMessage(){

	}

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	@Override
	public String toString() {
		return "FeedMessage [title=" + title + ", description=" + description + ", link=" + link + ", author=" + author
				+ ", guid=" + guid + ", pubDate=" + pubDate + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(link);
		dest.writeString(author);
		dest.writeString(guid);
		dest.writeString(pubDate);
	}

	protected FeedMessage(Parcel in) {
		title = in.readString();
		description = in.readString();
		link = in.readString();
		author = in.readString();
		guid = in.readString();
		pubDate = in.readString();
	}

	public static final Creator<FeedMessage> CREATOR = new Creator<FeedMessage>() {
		@Override
		public FeedMessage createFromParcel(Parcel in) {
			return new FeedMessage(in);
		}

		@Override
		public FeedMessage[] newArray(int size) {
			return new FeedMessage[size];
		}
	};
}