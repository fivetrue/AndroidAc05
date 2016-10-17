// IFirebaseChattingCallback.aidl
package com.fivetrue.gimpo.ac05.chatting;

// Declare any non-default types here with import statements
import com.fivetrue.gimpo.ac05.chatting.ChatMessage;

interface IChattingCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onReceivedMessage(int type, String key, in ChatMessage msg);
}
