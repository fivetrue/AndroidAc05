package com.fivetrue.gimpo.ac05.vo.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TownDataEntry implements Parcelable{
	
	private int count = 0;
	private String description = null;
	private String title = null;
	private String titleColor = null;
	private String titleBgColor = null;
	private String contentColor = null;
	private String contentBgColor = null;
	private ArrayList<TownData> list = new ArrayList<>();

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public ArrayList<TownData> getList() {
		return list;
	}
	public void setList(ArrayList<TownData> list) {
		this.list = list;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "TownDataEntry{" +
				"count=" + count +
				", description='" + description + '\'' +
				", title='" + title + '\'' +
				", titleColor='" + titleColor + '\'' +
				", titleBgColor='" + titleBgColor + '\'' +
				", contentColor='" + contentColor + '\'' +
				", contentBgColor='" + contentBgColor + '\'' +
				", list=" + list +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(count);
		dest.writeString(description);
		dest.writeString(title);
		dest.writeString(titleColor);
		dest.writeString(titleBgColor);
		dest.writeString(contentColor);
		dest.writeString(contentBgColor);
		dest.writeTypedList(list);
	}

	protected TownDataEntry(Parcel in) {
		count = in.readInt();
		description = in.readString();
		title = in.readString();
		titleColor = in.readString();
		titleBgColor = in.readString();
		contentColor = in.readString();
		contentBgColor = in.readString();
		list = in.createTypedArrayList(TownData.CREATOR);
	}

	public static final Creator<TownDataEntry> CREATOR = new Creator<TownDataEntry>() {
		@Override
		public TownDataEntry createFromParcel(Parcel in) {
			return new TownDataEntry(in);
		}

		@Override
		public TownDataEntry[] newArray(int size) {
			return new TownDataEntry[size];
		}
	};
}
