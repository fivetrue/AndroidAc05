package com.fivetrue.gimpo.ac05.firebase.model;

import com.fivetrue.gimpo.ac05.firebase.FirebaseData;

import java.util.ArrayList;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class AppConfig extends FirebaseData {

    public int appVersionCode;
    public long clubId;
    public String clubUrl;
    public String clubMyInfo;
    public long senderId;
    public String defaultImageUrl;
    public boolean forceUpdate;
    public ArrayList<District> districts;
}
