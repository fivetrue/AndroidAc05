package com.fivetrue.gimpo.ac05.firebase.database;

import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;
import com.fivetrue.gimpo.ac05.firebase.model.ImageInfo;
import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by kwonojin on 2016. 10. 23..
 */

public class ImageInfoDatabase extends BaseFirebaseReference<ImageInfo> {


    private static final String PATH = "/static/imageinfo/";

    public ImageInfoDatabase() {
        super(PATH);
    }


}
