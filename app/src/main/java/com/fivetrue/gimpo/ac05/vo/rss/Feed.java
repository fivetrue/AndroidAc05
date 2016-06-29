package com.fivetrue.gimpo.ac05.vo.rss;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/*
 * Stores an RSS feed
 */
public class Feed implements Parcelable{

	final String title;
	final String link;
	final String description;
	final String language;
	final String copyright;
	final String pubDate;
	final String lastBuildDate;

	List<FeedMessage> entries = new ArrayList<>();

	public Feed(String title, String link, String description, String language,
			String copyright, String pubDate, String lastBuildDate) {
		this.title = title;
		this.link = link;
		this.description = description;
		this.language = language;
		this.copyright = copyright;
		this.pubDate = pubDate;
		this.lastBuildDate = lastBuildDate;
	}

	protected Feed(Parcel in) {
		title = in.readString();
		link = in.readString();
		description = in.readString();
		language = in.readString();
		copyright = in.readString();
		pubDate = in.readString();
		lastBuildDate = in.readString();
		entries = in.createTypedArrayList(FeedMessage.CREATOR);
	}

	public static final Creator<Feed> CREATOR = new Creator<Feed>() {
		@Override
		public Feed createFromParcel(Parcel in) {
			return new Feed(in);
		}

		@Override
		public Feed[] newArray(int size) {
			return new Feed[size];
		}
	};

	public List<FeedMessage> getMessages() {
		return entries;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	public String getLanguage() {
		return language;
	}

	public String getCopyright() {
		return copyright;
	}

	public String getPubDate() {
		return pubDate;
	}

	public String getLastBuildDate() {
		return lastBuildDate;
	}

	@Override
	public String toString() {
		return "Feed [title=" + title + ", link=" + link + ", description=" + description + ", language=" + language
				+ ", copyright=" + copyright + ", pubDate=" + pubDate + ", lastBuildDate=" + lastBuildDate + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(link);
		dest.writeString(description);
		dest.writeString(language);
		dest.writeString(copyright);
		dest.writeString(pubDate);
		dest.writeString(lastBuildDate);
		dest.writeTypedList(entries);
	}
}