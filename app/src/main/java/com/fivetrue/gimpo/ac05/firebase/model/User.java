package com.fivetrue.gimpo.ac05.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fivetrue.gimpo.ac05.firebase.FirebaseData;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class User extends FirebaseData implements Parcelable{

    public String uid;
    public String email;
    public String nickName;
    public String name;
    public String providerId;

    public String profileImage;
    public String gcmId;

    public String device;

    public int district;

    public User(){}

    public User(FirebaseUser user, String gcm, String device, int district){
        uid = user.getUid();
        email = user.getEmail();
        providerId = user.getProviderId();
        name = user.getDisplayName();
        profileImage = user.getPhotoUrl().toString();
        this.gcmId = gcm;
        this.device = device;
        this.district = district;
    }

    public String getDisplayName(){
        return TextUtils.isEmpty(nickName) ? name : nickName;
    }

    protected User(Parcel in) {
        uid = in.readString();
        email = in.readString();
        nickName = in.readString();
        name = in.readString();
        providerId = in.readString();
        profileImage = in.readString();
        gcmId = in.readString();
        device = in.readString();
        district = in.readInt();
        updateTime = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(email);
        dest.writeString(nickName);
        dest.writeString(name);
        dest.writeString(providerId);
        dest.writeString(profileImage);
        dest.writeString(gcmId);
        dest.writeString(device);
        dest.writeInt(district);
        dest.writeLong(updateTime);
    }
}
