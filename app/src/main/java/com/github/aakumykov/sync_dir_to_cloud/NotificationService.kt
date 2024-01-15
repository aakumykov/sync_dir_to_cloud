package com.github.aakumykov.sync_dir_to_cloud

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskState
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskNotificator

class NotificationService : Service() {

    private var syncTaskStateLiveData: LiveData<TaskState>? = null
    private var syncTaskStateObserver: Observer<TaskState>? = null
    private val syncTaskNotificator: SyncTaskNotificator by lazy {
        App.getAppComponent().getSyncTaskNotificator()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.getStringExtra(TASK_ID)?.also { taskId ->
            preparePublisherAndObserver(taskId)
            startWatchingToState()
        }
            ?: Log.e(TAG, "Task id not found in Intent")

        return START_NOT_STICKY
    }

    private fun preparePublisherAndObserver(taskId: String) {
        Log.d(TAG, "preparePublisherAndObserver() called with: taskId = $taskId")
        syncTaskStateLiveData = App.getAppComponent().getSyncTaskStateReader().getSyncTaskStateAsLiveData(taskId)
        syncTaskStateObserver = Observer { taskState ->
            Log.d(TAG, "(service hc: ${serviceHashCode()}) $taskState")
            syncTaskNotificator.showNotification(taskState)
        }
    }

    private fun startWatchingToState() {
//        Log.d(TAG, "startWatchingToState() called")
        syncTaskStateLiveData!!.observeForever(syncTaskStateObserver!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")

        if (null != syncTaskStateLiveData && null != syncTaskStateObserver)
            syncTaskStateLiveData!!.removeObserver(syncTaskStateObserver!!)

        syncTaskStateLiveData = null
        syncTaskStateObserver = null
    }

    override fun onBind(intent: Intent?): IBinder? { TODO("Not yet implemented") }

    private fun serviceHashCode(): String = hashCode().toString()

    companion object {
        val TAG: String = NotificationService::class.java.simpleName
        const val TASK_ID = "TASK_ID"

        fun start(context: Context, taskId: String) {
            context.startService(
                intent(context).apply {
                    putExtra(TASK_ID, taskId)
                }
            )
        }

        fun stop(context: Context) { context.stopService(intent(context)) }

        private fun intent(context: Context): Intent = Intent(context, NotificationService::class.java)
    }
}
