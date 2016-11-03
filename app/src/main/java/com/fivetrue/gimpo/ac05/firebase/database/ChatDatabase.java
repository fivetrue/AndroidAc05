package com.fivetrue.gimpo.ac05.firebase.database;

import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;
import com.fivetrue.gimpo.ac05.firebase.model.ChatMessage;

/**
 * Created by kwonojin on 2016. 10. 23..
 */

public class ChatDatabase extends BaseFirebaseReference<ChatMessage> {

    private static final String PATH = "/chatting/%s/";


    public ChatDatabase(String id) {
        super(String.format(PATH, id));
    }

}
