package com.fivetrue.gimpo.ac05.firebase.database;

import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;
import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by kwonojin on 2016. 10. 23..
 */

public class ScrapContentDatabase extends BaseFirebaseReference<ScrapContent> {


    private static final String PATH = "/data/scrap/";
    public static final String COMMENT = "comment";

    public ScrapContentDatabase() {
        super(PATH);
    }

    public Query isExistCapturedPage(String targetUrl){
        return getReference().orderByChild("url").equalTo(targetUrl).limitToFirst(1);
    }

    public Query loadCapturedPage(int limit){
        return getReference().orderByChild("updateTime").limitToFirst(limit);
    }

    public DatabaseReference getCommentReference(String scrapKey){
        return getReference().child(scrapKey).child(COMMENT).getRef();
    }

}
