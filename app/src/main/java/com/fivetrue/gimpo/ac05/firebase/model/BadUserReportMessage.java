package com.fivetrue.gimpo.ac05.firebase.model;

import com.fivetrue.gimpo.ac05.firebase.FirebaseData;
import com.fivetrue.gimpo.ac05.firebase.MessageData;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class BadUserReportMessage extends FirebaseData implements MessageData {

    public final String message;
    public final User user;

    public BadUserReportMessage(String message, User user) {
        this.message = message;
        this.user = user;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public User getUser() {
        return null;
    }
}
