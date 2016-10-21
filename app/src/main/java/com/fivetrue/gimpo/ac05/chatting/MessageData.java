package com.fivetrue.gimpo.ac05.chatting;

import com.fivetrue.gimpo.ac05.firebase.model.User;

import java.util.HashMap;

/**
 * Created by kwonojin on 2016. 10. 16..
 */

public interface MessageData {

    String getMessage();

    String getImageUrl();

    User getUser();
}
