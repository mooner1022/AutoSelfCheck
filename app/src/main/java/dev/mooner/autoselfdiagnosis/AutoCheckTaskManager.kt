package dev.mooner.autoselfdiagnosis

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.mooner.autoselfdiagnosis.Utils.Companion.isWeekdays
import dev.mooner.autoselfdiagnosis.objects.Config
import dev.mooner.autoselfdiagnosis.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*


object AutoCheckTaskManager {

    private const val NOTIFICATION_ID = 2
    private const val CHANNEL_ID = "AutoCheckNotificationChannel"
    private lateinit var alarmManager: AlarmManager
    private const val TAG = "AutoCheckTaskManager"

    private fun getIntent(context: Context): Intent {
        return Intent(context, AlarmBroadcastReceiver::class.java).apply {
            action = Const.ALARM_ACTION
        }
    }

    fun setAlarm(context: Context, hour: Int, minute: Int, force: Boolean = false) {
        val time = Calendar.getInstance().apply {
            if (get(Calendar.DAY_OF_WEEK + 1).isWeekdays()) {
                if (get(Calendar.HOUR_OF_DAY) >= hour && get(Calendar.MINUTE) >= minute) {
                    add(Calendar.DATE, 1)
                    println("next day")
                }
            } else {
                add(Calendar.DATE, 3)
            }
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        Session.nextAlarmTime = time.timeInMillis

        if (!force) {
            if (isAlarmSet(context)) {
                Log.d(TAG, "Alarm set ignored (Alarm already set)")
                return
            }
        }

        val intent = getIntent(context)
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingIntent)
    }

    fun isAlarmSet(context: Context): Boolean {
        return PendingIntent.getBroadcast(
            context, 0,
            getIntent(context),
            PendingIntent.FLAG_NO_CREATE
        ) != null
    }

    fun cancelAlarm(context: Context) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0,
            getIntent(context),
            PendingIntent.FLAG_NO_CREATE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun showNotification(context: Context) {
        val pendingIntent: PendingIntent =
            Intent(context, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(context, 0, notificationIntent, 0)
            }
        val calendar = Calendar.getInstance()
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSubText("???????????? ??????!")
                .setContentTitle("${calendar.get(Calendar.HOUR_OF_DAY)}??? ${calendar.get(Calendar.MINUTE)}?????? ???????????? ??????????????? ???????????????!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
                .build()
        } else {
            NotificationCompat.Builder(context)
                .setSubText("???????????? ??????!")
                .setContentTitle("${calendar.get(Calendar.HOUR_OF_DAY)}??? ${calendar.get(Calendar.MINUTE)}?????? ???????????? ??????????????? ???????????????!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
                .build()
        }
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "???????????? ?????? ??????",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            //notificationChannel.enableLights(false)
            //notificationChannel.enableVibration(false)
            //notificationChannel.description = "StarLight ??? ???????????? ???????????? ?????? ???????????????."

            val notificationManager = context.getSystemService(
                Service.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(
                notificationChannel)
        }
    }

    internal class AlarmBroadcastReceiver: BroadcastReceiver() {

        private lateinit var checker: SelfChecker

        override fun onReceive(context: Context, intent: Intent) {
            val config: Config = Json.decodeFromString(context.getSharedPreferences(Const.PREF_NAME, 0).getString("config", "")!!)
            CoroutineScope(Dispatchers.Default).launch {
                checker = SelfChecker(config)
                checker.check()
            }
            createNotificationChannel(context)
            showNotification(context)
            setAlarm(context, config.hour, config.minute)
        }

    }
}