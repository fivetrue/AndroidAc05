package com.fivetrue.gimpo.ac05.firebase.database;

import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class ActiveChatUserDatabase extends BaseFirebaseReference<User> {

    public static final String PATH = "/active/chat/%s";

    public ActiveChatUserDatabase(String type) {
        super(String.format(PATH, type));
    }

    @Override
    protected DatabaseReference getUpdateTimeRef() {
        return getReference().child("updateTime").getRef();
    }
}
