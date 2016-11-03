package com.fivetrue.gimpo.ac05.firebase.database;

import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;
import com.fivetrue.gimpo.ac05.firebase.model.ChatMessage;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class MessageBoxDatabase extends BaseFirebaseReference<ChatMessage> {

    public static final String PATH = "/messageBox/user/%s";
    public static final String PERSON = "person";
    public static final String NOTIFY = "notify";

    public MessageBoxDatabase(String uid) {
        super(String.format(PATH, uid));
    }

    public DatabaseReference getPersonReference(){
        return getReference().child(PERSON);
    }

    public DatabaseReference getNotifyReference(){
        return getReference().child(NOTIFY);
    }
}
