// IFirebaseChttingService.aidl
package com.fivetrue.gimpo.ac05.service;

// Declare any non-default types here with import statements
import com.fivetrue.gimpo.ac05.service.IFirebaseServiceCallback;

interface IFirebaseService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

     boolean registerCallback(IFirebaseServiceCallback callback);
     boolean unregisterCallback(IFirebaseServiceCallback callback);
}
