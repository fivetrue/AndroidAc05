package com.fivetrue.gimpo.ac05.vo.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.fivetrue.gimpo.ac05.vo.IBaseItem;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageInfo implements Parcelable, IBaseItem{
	
	private String imageUrl = null;
	private String imageName = null;
	private String description = null;
	private String imageType = null;
	private int number = 0;
	private long createTime = 0;

	public ImageInfo(){

	}

	public ImageInfo(String url, String name, String desc, String type){
		this.imageUrl = url;
		this.imageName = name;
		this.description = desc;
		this.imageType = type;
	}

	protected ImageInfo(Parcel in) {
		imageUrl = in.readString();
		imageName = in.readString();
		description = in.readString();
		imageType = in.readString();
		number = in.readInt();
		createTime = in.readLong();
	}

	public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
		@Override
		public ImageInfo createFromParcel(Parcel in) {
			return new ImageInfo(in);
		}

		@Override
		public ImageInfo[] newArray(int size) {
			return new ImageInfo[size];
		}
	};

	public String getImageUrl() {
		return imageUrl;
	}

	@Override
	public String getTitle() {
		return imageName;
	}

	@Override
	public String getContent() {
		return description;
	}

	@Override
	public String getSubContent() {
		return  new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(getTime()));
	}

	@Override
	public long getTime() {
		return createTime;
	}

	@Override
	public String getUrl() {
		return imageUrl;
	}

	@Override
	public boolean isShowingNew() {
		return (System.currentTimeMillis() - (ONE_DAY * 3)) < getTime();
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getImageType() {
		return imageType;
	}
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	
	public int getIndex() {
		return number;
	}

	public void setIndex(int index) {
		this.number = index;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "ImageInfo{" +
				"imageUrl='" + imageUrl + '\'' +
				", imageName='" + imageName + '\'' +
				", description='" + description + '\'' +
				", imageType='" + imageType + '\'' +
				", number=" + number +
				", createTime=" + createTime +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(imageUrl);
		dest.writeString(imageName);
		dest.writeString(description);
		dest.writeString(imageType);
		dest.writeInt(number);
		dest.writeLong(createTime);
	}

}
