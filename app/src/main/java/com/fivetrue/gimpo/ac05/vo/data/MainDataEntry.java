package com.fivetrue.gimpo.ac05.vo.data;

import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;

import java.util.ArrayList;

public class MainDataEntry {

	private ArrayList<NotificationData> notices = new ArrayList<>();
	private ArrayList<NotificationData> notification = new ArrayList<>();
	private TownDataEntry town = null;
	private ArrayList<PageData> pages = new ArrayList<>();
	private ArrayList<ImageInfo> imageInfos = new ArrayList<>();

	public ArrayList<NotificationData> getNotices() {
		return notices;
	}

	public void setNotices(ArrayList<NotificationData> notices) {
		this.notices = notices;
	}

	public TownDataEntry getTown() {
		return town;
	}

	public void setTown(TownDataEntry town) {
		this.town = town;
	}

	public ArrayList<PageData> getPages() {
		return pages;
	}

	public void setPages(ArrayList<PageData> pages) {
		this.pages = pages;
	}

	public ArrayList<NotificationData> getNotification() {
		return notification;
	}

	public void setNotification(ArrayList<NotificationData> notification) {
		this.notification = notification;
	}

	public ArrayList<ImageInfo> getImageInfos() {
		return imageInfos;
	}

	public void setImageInfos(ArrayList<ImageInfo> imageInfos) {
		this.imageInfos = imageInfos;
	}

	@Override
	public String toString() {
		return "MainDataEntry{" +
				"notices=" + notices +
				", town=" + town +
				", pages=" + pages +
				'}';
	}
}
