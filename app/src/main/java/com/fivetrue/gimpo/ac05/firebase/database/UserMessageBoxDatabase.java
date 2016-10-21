package com.fivetrue.gimpo.ac05.firebase.database;

import com.fivetrue.gimpo.ac05.chatting.ChatMessage;
import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class UserMessageBoxDatabase extends BaseFirebaseReference<ChatMessage> {

    public static final String PATH = "/messageBox/user/%s";

    public UserMessageBoxDatabase(String uid) {
        super(String.format(PATH, uid));
    }
}
