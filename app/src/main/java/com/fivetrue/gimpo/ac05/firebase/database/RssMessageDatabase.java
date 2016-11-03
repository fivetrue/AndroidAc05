package com.fivetrue.gimpo.ac05.firebase.database;

import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;
import com.fivetrue.gimpo.ac05.firebase.model.RssMessage;

/**
 * Created by kwonojin on 2016. 10. 23..
 */

public class RssMessageDatabase extends BaseFirebaseReference<RssMessage> {

    private static final String PATH = "/data/rss/";


    public RssMessageDatabase() {
        super(PATH);
    }

}
