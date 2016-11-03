package com.fivetrue.gimpo.ac05.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class AdFragment extends Fragment{

    public interface OnMyListener{
        void onReceivedData(Object data);
    }

    public static Fragment newInstance(MyData data){
        Fragment f = new AdFragment();
        Bundle b = new Bundle();
        b.putParcelable(MyData.class.getName(), data);
        f.setArguments(b);
        return f;
    }






    public static class MyData implements Parcelable{

        public String name;
        public String address;

        protected MyData(Parcel in) {
            name = in.readString();
            address = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(address);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<MyData> CREATOR = new Creator<MyData>() {
            @Override
            public MyData createFromParcel(Parcel in) {
                return new MyData(in);
            }

            @Override
            public MyData[] newArray(int size) {
                return new MyData[size];
            }
        };
    }

}



