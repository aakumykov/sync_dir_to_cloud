package com.github.aakumykov.sync_dir_to_cloud.utils

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.IBinder
import android.util.Log
import androidx.appcompat.resources.R
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.IconCompat
import com.github.aakumykov.sync_dir_to_cloud.utils.SampleService.Companion.PARAM_NOTIFICATION_MESSAGE
import com.github.aakumykov.sync_dir_to_cloud.utils.SampleService.Companion.PARAM_NOTIFICATION_TITLE
import com.github.aakumykov.sync_dir_to_cloud.utils.SampleService.Companion.PARAM_SHOW_NOTIFICATION
import com.github.aakumykov.sync_dir_to_cloud.utils.SampleService.Companion.PARAM_WORKING_TIME_SEC
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.random.Random
import androidx.core.graphics.drawable.toDrawable

/**
 * "Простая служба".
 * Доступные для передачи параметры запускающего Intent:
 * [PARAM_SHOW_NOTIFICATION] - показывать уведомление о работе.
 * [PARAM_WORKING_TIME_SEC] - если установлен, служба работает указанное количество секунд и самостоятельно завершается.
 * [PARAM_NOTIFICATION_TITLE] - заголовок уведомления.
 * [PARAM_NOTIFICATION_MESSAGE] - текст уведомления.
 */
class SampleService : Service() {

    private var currentIntent: Intent? = null

    private val notificationTitle: String get() = currentIntent?.getStringExtra(PARAM_NOTIFICATION_TITLE) ?: DEFAULT_NOTIFICATION_TITLE
    private val notificationMessage: String get() = currentIntent?.getStringExtra(PARAM_NOTIFICATION_MESSAGE) ?: DEFAULT_NOTIFICATION_MESSAGE
    private val notificationIcon: Int get() = currentIntent?.getIntExtra(PARAM_NOTIFICATION_ICON, DEFAULT_NOTIFICATION_ICON) ?: DEFAULT_NOTIFICATION_ICON
    private val needToShowNotification: Boolean get() = currentIntent?.getBooleanExtra(PARAM_SHOW_NOTIFICATION, DEFAULT_SHOW_NOTIFICATION) ?: DEFAULT_SHOW_NOTIFICATION
    private val workingTimeSec: Int get() = currentIntent?.getIntExtra(PARAM_WORKING_TIME_SEC, 0) ?: 0
    private val stopActionText: String get() = currentIntent?.getStringExtra(PARAM_STOP_ACTION_TEXT) ?: DEFAULT_STOP_ACTION_TEXT

    private val channelId: String get() {
        return currentIntent?.getStringExtra(PARAM_CHANNEL_ID) ?: DEFAULT_CHANNEL_ID
    }

    private val channelName: String get() {
        return currentIntent?.getStringExtra(PARAM_CHANNEL_NAME)
            ?: DEFAULT_CHANNEL_NAME
    }

    private val channelDescription: String get() {
        return currentIntent?.getStringExtra(PARAM_CHANNEL_DESCRIPTION)
            ?: DEFAULT_CHANNEL_DESCRIPTION
    }

    private val channelImportance: Int get() {
        return currentIntent?.getIntExtra(PARAM_CHANNEL_IMPORTANCE, DEFAULT_CHANNEL_IMPORTANCE)
            ?: DEFAULT_CHANNEL_IMPORTANCE
    }

    override fun onBind(intent: Intent): IBinder? = null


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
    }


    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d(TAG, "onStartCommand()")

        // Всё, что получает данные из Intent-а, должно запускаться после.
        currentIntent = intent

        return when(intent?.action) {
            ACTION_START -> startRunning()
            ACTION_STOP -> stopRunning()
            else -> super.onStartCommand(intent, flags, startId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }


    private fun startRunning(): Int {
        Log.d(TAG, "startRunning()")

        if (!isRunning.get()) {
            isRunning.set(true)

            if (needToShowNotification) {
                reCreateChannel()
                startForeground(FOREGROUND_SERVICE_ID, initialNotification())
            }

            doFakeWork()
        }
        // FIXME: какое значение корректно использовать здесь?
        return START_REDELIVER_INTENT
    }


    private fun doFakeWork() {
        if (workingTimeSec > 0) {
            thread {
                try { repeat(workingTimeSec) { i ->
                    if (isRunning.get()) {
                        Log.d(TAG, "Работа ${i + 1} секунд...")
                        TimeUnit.SECONDS.sleep(1)
                    } else {
                        return@thread
                    }
                } } finally {
                    stopRunning()
                }
            }
        }
    }


    private fun stopRunning(): Int {
        Log.d(TAG, "stopRunning()")
        stopSelf()
        isRunning.set(false)
        return START_NOT_STICKY
    }

    private fun reCreateChannel() {
        NotificationChannelHelper(applicationContext).reCreateChannel(
            channelId,
            channelName,
            channelDescription,
            channelImportance
        )
    }


    private fun initialNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(notificationTitle)
            .setContentText(notificationMessage)
            .setSmallIcon(notificationIcon)
//            .setContentIntent(MainActivity.pendingIntent(applicationContext))
            .addAction(createStopServiceAction())
            .build()
    }


    private fun createStopServiceAction(): NotificationCompat.Action {
        return NotificationCompat.Action(
            IconCompat.createFromIcon(Icon.createWithData(byteArrayOf(),0,0)),//android.R.drawable.ic_dialog_info,
            stopActionText,
            pendingIntent(stopIntent(applicationContext), CODE_STOP_SERVICE)
        )
    }


    private fun pendingIntent(intent: Intent, code: Int): PendingIntent {
        return PendingIntent.getService(applicationContext, code, intent, PendingIntent.FLAG_IMMUTABLE)
    }


    companion object {

        val isRunning: AtomicBoolean = AtomicBoolean(false)

        private const val DEFAULT_SHOW_NOTIFICATION: Boolean = true

        val TAG: String = SampleService::class.java.simpleName

        const val PARAM_SHOW_NOTIFICATION = "SHOW_NOTIFICATION"
        const val PARAM_NOTIFICATION_TITLE = "NOTIFICATION_TITLE"
        const val PARAM_NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE"
        const val PARAM_NOTIFICATION_ICON = "NOTIFICATION_ICON"

              val DEFAULT_NOTIFICATION_TITLE = TAG
        const val DEFAULT_NOTIFICATION_MESSAGE = "запущена"
        const val DEFAULT_NOTIFICATION_ICON = android.R.drawable.ic_dialog_info

        const val PARAM_CHANNEL_ID = "CHANNEL_ID"
        const val PARAM_CHANNEL_NAME = "CHANNEL_NAME"
        const val PARAM_CHANNEL_DESCRIPTION = "CHANNEL_DESCRIPTION"
        const val PARAM_CHANNEL_IMPORTANCE = "CHANNEL_IMPORTANCE"

        const val DEFAULT_CHANNEL_ID = "SERVICE_DEFAULT_CHANNEL"
        const val DEFAULT_CHANNEL_NAME: String = "Notifications"
              val DEFAULT_CHANNEL_DESCRIPTION: String = "$TAG notifications"
        const val DEFAULT_CHANNEL_IMPORTANCE = NotificationManagerCompat.IMPORTANCE_LOW

        const val PARAM_STOP_ACTION_TEXT = "STOP_ACTION_TEXT"
        const val PARAM_STOP_ACTION_ICON = "STOP_ACTION_ICON"
        const val DEFAULT_STOP_ACTION_TEXT = "Stop"

        const val PARAM_WORKING_TIME_SEC = "PARAM_WORKING_TIME"

        const val ACTION_START = "START"
        const val ACTION_STOP = "STOP"

        const val CODE_STOP_SERVICE = 10
        const val FOREGROUND_SERVICE_ID = 1

        fun start(context: Context) = context.startService(selfIntentWithAction(context, ACTION_START))
        fun stop(context: Context) = context.startService(stopIntent(context))

        fun selfIntent(context: Context): Intent = Intent(context, SampleService::class.java)

        private fun stopIntent(context: Context): Intent = selfIntentWithAction(context, ACTION_STOP)

        private fun selfIntentWithAction(context: Context, action: String): Intent {
            return selfIntent(context).apply { setAction(action) }
        }

        private val STOP_ACTION_BITMAP_DATA: ByteArray = ByteArray(0)
    }
}