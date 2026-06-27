package com.github.aakumykov.sync_dir_to_cloud.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.GlobalKeys.KEY_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.showToast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncTaskService : Service() {

    @Inject
    lateinit var syncTaskExecutorFactory: SyncTaskExecutorAssistedFactory

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel(CancellationException("SyncTaskService().onDestroy()"))
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.injectToSyncTaskService(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when(intent?.action) {
            ACTION_START -> startWork(intent)
            ACTION_CANCEL -> cancelWork(intent)
            else -> super.onStartCommand(intent, flags, startId)
        }
    }

    private fun startWork(intent: Intent): Int {
        val taskId = intent.getStringExtra(KEY_TASK_ID)

        if (null == taskId) {
            showError(R.string.error_there_is_no_task_id)
            return START_NOT_STICKY
        }

        serviceScope.launch {
            syncTaskExecutorFactory
                .create(taskId)
                .executeSyncTask(this, taskId)
        }

        return START_STICKY
    }

    private fun cancelWork(intent: Intent): Int {
        stopSelf()
        return START_NOT_STICKY
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

        val TAG: String = SyncTaskService::class.java.simpleName
        const val ACTION_START = "ACTION_STOP"
        const val ACTION_CANCEL = "ACTION_CANCEL"
    }
}