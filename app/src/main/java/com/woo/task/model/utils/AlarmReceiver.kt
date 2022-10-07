package com.woo.task.model.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.woo.task.R
import com.woo.task.view.ui.activities.SplashActivity
import com.woo.task.viewmodel.TasksViewModel
import java.util.*

class AlarmReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 2626
        const val NOTIFICATION_CHANNEL_ID = "2625"
    }

    lateinit var tasksViewModel: TasksViewModel

    override fun onReceive(context: Context, intent: Intent) {
        val code = intent.getIntExtra("requestCode", 0)
        if ((Intent.ACTION_BOOT_COMPLETED) == intent.action){

        }else{
            val text = intent.getStringExtra("text") ?: context.getString(R.string.alarm_notification_title)
            createNotificationChannel(context)
            notifyNotification(context,text)
        }

    }

    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "WooTaskAlarm",
                NotificationManager.IMPORTANCE_HIGH
            )

            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    private fun notifyNotification(context: Context,text:String) {
        val contentIntent = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val contentPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context,
                UUID.randomUUID().hashCode(),
                contentIntent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                UUID.randomUUID().hashCode(),
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        with(NotificationManagerCompat.from(context)) {
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.alarm_text))
                .setContentText(text)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(text)
                )
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notify(NOTIFICATION_ID, build.build())
        }
    }
}