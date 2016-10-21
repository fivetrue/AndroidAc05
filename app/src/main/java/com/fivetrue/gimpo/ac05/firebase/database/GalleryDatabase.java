package com.fivetrue.gimpo.ac05.firebase.database;

import android.content.Context;

import com.fivetrue.gimpo.ac05.chatting.GalleryMessage;
import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class GalleryDatabase extends BaseFirebaseReference<GalleryMessage> {

    public static final String PATH = "/gallery";

    public GalleryDatabase() {
        super(PATH);
    }
}
