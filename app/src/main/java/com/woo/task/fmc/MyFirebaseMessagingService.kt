package com.woo.task.fmc

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.woo.task.view.utils.AppPreferences


class MyFirebaseMessagingService: FirebaseMessagingService() {
    val TAG = "FIREBASENOTIFY"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from);
        AppPreferences.setup(this)

        if(AppPreferences.notifications!!){
            Log.d(TAG, "SHOWING NOTIFICATIONS!");
        }else{
            Log.d(TAG, "DONT SHOW NOTIFICATIONS!");
        }

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body);
        }

    }

    override fun onNewToken(token: String) {

        Log.d(TAG, "Refreshed token: $token")




    }

}