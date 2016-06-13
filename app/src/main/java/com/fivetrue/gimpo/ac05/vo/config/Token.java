package com.fivetrue.gimpo.ac05.vo.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.fivetrue.gimpo.ac05.utils.Log;

/**
 * Created by kwonojin on 16. 6. 13..
 */
public class Token implements Parcelable{

    private static final String TAG = "Token";

    private String access_token	= null; //string	접근 토큰, 발급 후 expires_in 파라미터에 설정된 시간(초)이 지나면 만료됨
    private String refresh_token = null; //	string	갱신 토큰, 접근 토큰이 만료될 경우 접근 토큰을 다시 발급받을 때 사용
    private String token_type = null; //	string	접근 토큰의 타입으로 Bearer와 MAC의 두 가지를 지원
    private int expires_in = 0; //	integer	접근 토큰의 유효 기간(초 단위)
    private String error = null; //	string	에러 코드
    private String error_description = null; //	string	에러 메시지

    private String result = null; // 삭제시 사용 성공하면 success 리턴

    private long updateTime = 0;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isExpired(){
        long expires_in_millis = expires_in * 1000;
        Log.i(TAG, "isExpired: updateTime = " + updateTime);
        Log.i(TAG, "isExpired: expries_in = " + expires_in);
        Log.i(TAG, "isExpired: expries_in millis= " + expires_in_millis);
        boolean b = (updateTime + expires_in_millis) > System.currentTimeMillis();
        Log.i(TAG, "isExpired: " + b);
        return b;
    }

    @Override
    public String toString() {
        return "Token{" +
                "access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_in=" + expires_in +
                ", error='" + error + '\'' +
                ", error_description='" + error_description + '\'' +
                ", result='" + result + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }

    protected Token(Parcel in) {
        access_token = in.readString();
        refresh_token = in.readString();
        token_type = in.readString();
        expires_in = in.readInt();
        error = in.readString();
        error_description = in.readString();
        result = in.readString();
        updateTime = in.readLong();
    }

    public static final Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(access_token);
        dest.writeString(refresh_token);
        dest.writeString(token_type);
        dest.writeInt(expires_in);
        dest.writeString(error);
        dest.writeString(error_description);
        dest.writeString(result);
        dest.writeLong(updateTime);

    }
}
