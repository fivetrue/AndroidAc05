package com.fivetrue.gimpo.ac05.firebase.database;

import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;
import com.fivetrue.gimpo.ac05.firebase.model.TownNews;

/**
 * Created by kwonojin on 2016. 11. 1..
 */

public class TownNewsDatabase extends BaseFirebaseReference<TownNews> {

    private static final String PATH = "/data/townNews/";

    public TownNewsDatabase() {
        super(PATH);
    }
}
