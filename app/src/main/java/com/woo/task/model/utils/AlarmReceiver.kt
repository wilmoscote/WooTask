package com.woo.task.model.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.woo.task.R
import com.woo.task.view.ui.activities.SplashActivity
import com.woo.task.view.utils.AppPreferences
import com.woo.task.viewmodel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class AlarmReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 2626
        const val NOTIFICATION_CHANNEL_ID = "2625"
    }

    override fun onReceive(context: Context, intent: Intent) {
        AppPreferences.setup(context)
        val code = intent.getIntExtra("requestCode", 0)


        if ((Intent.ACTION_BOOT_COMPLETED) == intent.action){
            Log.d("TASKDEBUG","RESTORING ALARMS!")
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager?
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S){
                if(alarmManager?.canScheduleExactAlarms() == false){
                    val intent2 = Intent().apply {
                        action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    }
                    context.startActivity(intent2)
                }
            }
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            for (alarm in AppPreferences.getAlarms()){
                val d = sdf.parse(alarm.alarmDate)
                val calendar = Calendar.getInstance()
                calendar.time = d!!
                Log.d("TASKDEBUG","Date: ${calendar.time}")
                val intent3 = Intent(context, AlarmReceiver::class.java)
                intent3.putExtra("text",alarm.alarmText)
                val pendingIntent = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                    PendingIntent.getBroadcast(context,alarm.alarmCode, intent3, PendingIntent.FLAG_UPDATE_CURRENT)
                }else{
                    PendingIntent.getBroadcast(context, alarm.alarmCode, intent3, PendingIntent.FLAG_MUTABLE)
                }
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }else{
            val id = intent.getIntExtra("id",0)
            val text = intent.getStringExtra("text") ?: context.getString(R.string.alarm_notification_title)
            val finalDate = intent.getStringExtra("date")
            Log.d(
                "TASKDEBUG",
                "Alarm received: $id - $text - $finalDate"
            )
            val alarmsList = AppPreferences.getAlarms()
            alarmsList.remove(
                AlarmModel(
                    id,
                    finalDate!!,
                    text
                )
            )
            Log.d(
                "TASKDEBUG",
                "Alarm deleted from Local: ${alarmsList.toString()}"
            )
            AppPreferences.setAlarms(alarmsList)

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
            notify(text.hashCode(), build.build())
        }
    }
}