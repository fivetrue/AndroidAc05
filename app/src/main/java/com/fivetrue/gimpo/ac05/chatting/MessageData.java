package com.fivetrue.gimpo.ac05.chatting;

import java.util.HashMap;

/**
 * Created by kwonojin on 2016. 10. 16..
 */

public interface MessageData {

    String getMessage();

    String getImageUrl();

    String getUserImage();

    String getUser();

    HashMap<String, Object> getValues();

}
