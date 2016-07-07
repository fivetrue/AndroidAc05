package com.fivetrue.gimpo.ac05.vo.user;

public class District {
	
	private int districtNumber = 0;
	private String districtName = null;
	private String districtType = null;
	private String districtDesc = null;
	private int count = 0;
	
	public int getDistrictNumber() {
		return districtNumber;
	}
	public void setDistrictNumber(int districtNumber) {
		this.districtNumber = districtNumber;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getDistrictType() {
		return districtType;
	}
	public void setDistrictType(String districtType) {
		this.districtType = districtType;
	}
	public String getDistrictDesc() {
		return districtDesc;
	}
	public void setDistrictDesc(String districtDesc) {
		this.districtDesc = districtDesc;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public String toString() {
		return "District [districtNumber=" + districtNumber + ", districtName=" + districtName + ", districtType="
				+ districtType + ", districtDesc=" + districtDesc + ", count=" + count + "]";
	}

	@Override
	public boolean equals(Object o) {
		boolean b = super.equals(o);
		if(!b){
			b = districtNumber == ((District)o).getDistrictNumber();
		}
		return b;
	}
}
