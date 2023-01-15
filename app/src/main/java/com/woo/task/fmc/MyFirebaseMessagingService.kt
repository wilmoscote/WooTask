package com.woo.task.fmc

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.woo.task.R
import com.woo.task.view.utils.AppPreferences


class MyFirebaseMessagingService: FirebaseMessagingService() {
    val TAG = "FIREBASENOTIFY"
        val NOTIFICATION_ID = 100
        val NOTIFICATION_CHANNEL_ID = "1001"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //Log.d(TAG, "From: " + remoteMessage.from);
        AppPreferences.setup(this)

        if(AppPreferences.notifications!!){
            createNotificationChannel(this)
            notifyNotification(this)
        }

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            //Log.d(TAG, "Message data payload: " + remoteMessage.data);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            //Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body);
        }

    }

    override fun onNewToken(token: String) {

        //Log.d(TAG, "Refreshed token: $token")
    }

    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "WooTaskNotification",
                NotificationManager.IMPORTANCE_HIGH
            )
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    private fun notifyNotification(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("WooTask")
                .setContentText(context.getString(R.string.alarm_text))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notify(NOTIFICATION_ID, build.build())
        }
    }
}