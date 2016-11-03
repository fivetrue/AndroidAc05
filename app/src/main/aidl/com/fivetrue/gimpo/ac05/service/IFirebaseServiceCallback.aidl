// IFirebaseChattingCallback.aidl
package com.fivetrue.gimpo.ac05.service;

// Declare any non-default types here with import statements
import com.fivetrue.gimpo.ac05.firebase.model.ChatMessage;
import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;

interface IFirebaseServiceCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onReceivedChat(int type, String key, in ChatMessage msg);

    void onReceivedScrapContent(in ScrapContent scrapContent);

    void onRefreshData();
}
