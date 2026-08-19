package com.github.aakumykov.sync_dir_to_cloud.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.aakumykov.sync_dir_to_cloud.GlobalKeys.KEY_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.config.NotificationChannelConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.showToast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncTaskService : Service() {

    @Inject
    lateinit var syncTaskExecutorFactory: SyncTaskExecutorAssistedFactory

    @Inject
    lateinit var syncTaskNotificatorAssistedFactory: SyncTaskNotificatorAssistedFactory

    @Inject
    lateinit var syncTaskReader: SyncTaskReader

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val notificationId: Int by lazy { View.generateViewId() }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onBind(p0: Intent?): IBinder? = null

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when(intent?.action) {
            ACTION_START -> startWork(intent)
            ACTION_CANCEL -> cancelWork()
            else -> super.onStartCommand(intent, flags, startId)
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.injectToSyncTaskService(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel(CancellationException("SyncTaskService().onDestroy()"))
    }

    private var job: Job? = null

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun startWork(intent: Intent): Int {

        val taskId = intent.getStringExtra(KEY_TASK_ID)

        if (null == taskId) {
            showError(R.string.error_there_is_no_task_id)
            return START_NOT_STICKY
        }

        val eh = CoroutineExceptionHandler { context, throwable ->
            // FIXME: как показывать уведомление здесь?
        }

        job = serviceScope.launch {

            val syncTask = syncTaskReader.getSyncTask(taskId)
            val executionId = newRandomId

            val notificator = syncTaskNotificatorAssistedFactory.create(
                taskId = taskId,
                executionId = executionId,
                notificationId = notificationId,
            )

            try {
                notificator.showProgressNotification(notificationId, syncTask, executionId)

                syncTaskExecutorFactory
                    .create(
                        syncTask = syncTask,
                        executionId = executionId,
                        notificator = notificator
                    )
                    .executeSyncTaskSimple(this)

                notificator.hideProgressNotification(notificationId)

            } catch (e: CancellationException) {
                notificator.showErrorNotification(e)
            }
        }

        return START_STICKY
    }

    private fun cancelWork(): Int {
        hideNotification()
        stopSelf()
        return START_NOT_STICKY
    }


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(startId: Int) {
        notificationManager.notify(
            startId, createNotification())
    }

    private fun hideNotification() {
        notificationId?.also {
            notificationManager.cancel(it)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NotificationChannelConfig.progress.channelId)
            .setContentTitle(getString(R.string.sync_task_progress_notification_title))
            .setSmallIcon(R.drawable.ic_sync_task_notification_progress)
            .setAutoCancel(true)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setUsesChronometer(true)
            .addAction(stopServiceAction())
            .build()
    }

    private fun stopServiceAction(): NotificationCompat.Action {
        return NotificationCompat.Action(
            R.drawable.ic_task_stop,
            this.getString(R.string.notification_action_cancel_task),
            pendingIntent(this, CODE_STOP_SERVICE)
        )
    }

    private fun showError(@StringRes messageId: Int) {
        getString(messageId).also {
            showToast(it)
            Log.e(TAG, it)
        }
    }

    companion object {

        fun intentForStart(context: Context, taskId: String): Intent {
            return Intent(context, SyncTaskService::class.java).apply {
                putExtra(KEY_TASK_ID, taskId)
                setAction(ACTION_START)
            }
        }

        fun intentForStop(context: Context): Intent {
            return Intent(context, SyncTaskService::class.java).apply {
//                putExtra(KEY_TASK_ID, taskId)
                setAction(ACTION_CANCEL)
            }
        }

        private fun pendingIntent(context: Context, requestCode: Int): PendingIntent {
            return PendingIntent.getService(
                context,
                requestCode,
                intent(context, ACTION_CANCEL),
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun intent(context: Context, action: String): Intent {
            return Intent(context, SyncTaskService::class.java).apply {
                setAction(action)
            }
        }

        val TAG: String = SyncTaskService::class.java.simpleName

        const val ACTION_START = "ACTION_START"
        const val ACTION_CANCEL = "ACTION_CANCEL"

        const val CODE_STOP_SERVICE = 1000
    }
}