package com.fivetrue.gimpo.ac05.vo.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class FirebaseUserInfo implements Parcelable{

    private String uid;
    private String email;
    private String providerId;
    private String displayName;
    private String photoUrl;

    private String gcmId;
    private String device;

    private int district;

    public FirebaseUserInfo(){}

    public FirebaseUserInfo(FirebaseUser user, String gcm, String device, int district){
        uid = user.getUid();
        email = user.getEmail();
        providerId = user.getProviderId();
        displayName = user.getDisplayName();
        photoUrl = user.getPhotoUrl().toString();
        this.gcmId = gcm;
        this.device = device;
        this.district = district;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getGcmId() {
        return gcmId;
    }

    public String getDevice() {
        return device;
    }

    public void setDistrict(int district){
        this.district = district;
    }

    public int getDistrict() {
        return district;
    }

    public String getName(){
        String name = "";
        if(email != null){
            name = email.substring(0, email.indexOf("@"));
        }
        return name;
    }

    protected FirebaseUserInfo(Parcel in) {
        uid = in.readString();
        email = in.readString();
        providerId = in.readString();
        displayName = in.readString();
        photoUrl = in.readString();
        gcmId = in.readString();
        device = in.readString();
        district = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(email);
        dest.writeString(providerId);
        dest.writeString(displayName);
        dest.writeString(photoUrl);
        dest.writeString(gcmId);
        dest.writeString(device);
        dest.writeInt(district);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FirebaseUserInfo> CREATOR = new Creator<FirebaseUserInfo>() {
        @Override
        public FirebaseUserInfo createFromParcel(Parcel in) {
            return new FirebaseUserInfo(in);
        }

        @Override
        public FirebaseUserInfo[] newArray(int size) {
            return new FirebaseUserInfo[size];
        }
    };
}
