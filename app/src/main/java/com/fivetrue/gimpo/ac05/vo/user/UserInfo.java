package com.fivetrue.gimpo.ac05.vo.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class UserInfo implements Parcelable{

    private String resultCode =  null;
    private String message = null;

    private String email = null;
    private String nickname = null;
    private String encId = null;
    private String profileImage = null;
    private String age = null;
    private String gender = null;
    private String id = null;
    private String name = null;
    private String birthday = null;

    private String gcmId = null;
    private String device = null;

    private int district = 0;

    public UserInfo(){

    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEncId() {
        return encId;
    }

    public void setEncId(String encId) {
        this.encId = encId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getDistrict() {
        return district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "resultCode='" + resultCode + '\'' +
                ", message='" + message + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", encId='" + encId + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", gcmId='" + gcmId + '\'' +
                ", device='" + device + '\'' +
                ", district=" + district +
                '}';
    }

    public UserInfo(Parcel p){
        resultCode = p.readString();
        message = p.readString();
        email = p.readString();
        nickname = p.readString();
        encId = p.readString();
        age = p.readString();
        gender = p.readString();
        id = p.readString();
        name = p.readString();
        birthday = p.readString();
        gcmId = p.readString();
        device = p.readString();
        district = p.readInt();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(resultCode);
        dest.writeString(message);
        dest.writeString(email);
        dest.writeString(nickname);
        dest.writeString(encId);
        dest.writeString(age);
        dest.writeString(gender);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(birthday);
        dest.writeString(gcmId);
        dest.writeString(device);
        dest.writeInt(district);
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
