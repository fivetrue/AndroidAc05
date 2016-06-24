package com.fivetrue.gimpo.ac05.vo.data;

import com.fivetrue.gimpo.ac05.service.notification.NotificationData;

import java.util.ArrayList;

public class MainDataEntry {

	private ArrayList<NotificationData> notices = new ArrayList<>();
	private TownDataEntry town = null;
	private ArrayList<PageData> pages = new ArrayList<>();

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

	@Override
	public String toString() {
		return "MainDataEntry{" +
				"notices=" + notices +
				", town=" + town +
				", pages=" + pages +
				'}';
	}
}
