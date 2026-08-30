package com.github.aakumykov.sync_dir_to_cloud.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
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
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.TaskJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.showToast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncTaskService : Service() {

    @Inject
    lateinit var syncTaskExecutorFactory: SyncTaskExecutorAssistedFactory

    @Inject
    lateinit var syncTaskNotificatorAssistedFactory: SyncTaskNotificatorAssistedFactory

    @Inject
    lateinit var syncTaskReader: SyncTaskReader

    private val taskJobsHolder: TaskJobsHolder by lazy { TaskJobsHolder }

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    // FIXME: определить значение [serviceJob].
    private val serviceJob = SupervisorJob()
    private val serviceDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val serviceScope = CoroutineScope(serviceDispatcher + serviceJob)

    override fun onBind(p0: Intent?): IBinder? = null

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when(intent?.action) {
            ACTION_START -> startTask(intent)
            ACTION_CANCEL -> cancelTask(intent)
            else -> super.onStartCommand(intent, flags, startId)
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.injectToSyncTaskService(this)

        /*while(taskJobsHolder.hasJobs()) {
            TimeUnit.SECONDS.sleep(1)
        }
        stopSelf()*/
    }

    override fun onDestroy() {
        serviceJob.cancel(CancellationException("SyncTaskService().onDestroy()"))
        super.onDestroy()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun startTask(intent: Intent): Int {

        val taskId = intent.getStringExtra(KEY_TASK_ID)

        if (null == taskId) {
            showError(R.string.error_there_is_no_task_id)
            return START_NOT_STICKY
        }

        val executionId = newRandomId
        val notificationId = View.generateViewId()

        val notificator = syncTaskNotificatorAssistedFactory.create(
            taskId = taskId,
            executionId = executionId,
            notificationId = notificationId,
        )

        makeServiceForeground(notificationId, notificator.progressNotification)

        val eh = CoroutineExceptionHandler { _, throwable ->
            notificator.showErrorNotification(throwable)
        }

        serviceScope.launch (serviceDispatcher + eh) {

            try {
                val syncTask = syncTaskReader.getSyncTask(taskId)

                notificator.showProgressNotification(notificationId, syncTask, executionId)

                syncTaskExecutorFactory
                    .create(
                        syncTask = syncTask,
                        executionId = executionId,
                        notificator = notificator
                    )
                    .executeSyncTaskSimple(this)

            }
            catch (e: CancellationException) {
                // TODO: что делать здесь?
                Log.e(TAG, e.errorMsg, e)
            }
            finally {
                taskJobsHolder.removeJob(taskId)
                notificator.hideProgressNotification(notificationId)
                if (taskJobsHolder.hasNoJobs())
                    stopSelf()
            }
        }.also {
            taskJobsHolder.addJob(taskId, it)
        }

        return START_STICKY
    }

    private fun makeServiceForeground(notificationId: Int, notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(notificationId, notification)
        }
    }

    private fun cancelTask(intent: Intent): Int {

        val taskId = intent.getStringExtra(KEY_TASK_ID)

        if (null == taskId) {
            showError(R.string.error_there_is_no_task_id)
            return START_NOT_STICKY
        }

        taskJobsHolder.getJob(taskId)?.cancel(CancellationException("Manual cancellation"))

        return START_NOT_STICKY
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