package com.fivetrue.gimpo.ac05.vo.data;

import java.util.ArrayList;

public class MainDataEntry {
	
	private TownDataEntry town = null;
	private ArrayList<PageData> pages = new ArrayList<>();
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
		return "MainDataEntry [town=" + town + ", pages=" + pages + "]";
	}
	
}
