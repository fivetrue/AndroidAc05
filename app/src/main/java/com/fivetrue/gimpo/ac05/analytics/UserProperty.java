package com.fivetrue.gimpo.ac05.analytics;

import android.util.Pair;

/**
 * Created by kwonojin on 16. 6. 26..
 */
public class UserProperty {
    public static final String DISTRICT = "district";

    public static Pair<String, String> makeProperty(String key, String value){
        return new Pair<>(key, value);
    }

}
