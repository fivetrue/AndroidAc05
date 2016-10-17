// IFirebaseChttingService.aidl
package com.fivetrue.gimpo.ac05.chatting;

// Declare any non-default types here with import statements
import com.fivetrue.gimpo.ac05.chatting.IChattingCallback;
import com.fivetrue.gimpo.ac05.chatting.ChatMessage;

interface IChattingService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

     boolean registerCallback(int type, IChattingCallback callback);
     boolean unregisterCallback(IChattingCallback callback);
     void sendMessage(int type, in ChatMessage msg);

}
