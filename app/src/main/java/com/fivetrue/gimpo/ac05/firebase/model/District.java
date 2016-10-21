package com.fivetrue.gimpo.ac05.firebase.model;

public class District {
	
	public int districtNumber = 0;
	public String districtName = null;
	public String districtType = null;
	public String districtDesc = null;
	public int count = 0;
	
	@Override
	public String toString() {
		return "District [districtNumber=" + districtNumber + ", districtName=" + districtName + ", districtType="
				+ districtType + ", districtDesc=" + districtDesc + ", count=" + count + "]";
	}

	@Override
	public boolean equals(Object o) {
		boolean b = super.equals(o);
		if(!b){
			b = districtNumber == ((District)o).districtNumber;
		}
		return b;
	}
}
