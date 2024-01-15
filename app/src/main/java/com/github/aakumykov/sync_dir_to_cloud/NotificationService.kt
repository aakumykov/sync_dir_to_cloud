package com.github.aakumykov.sync_dir_to_cloud

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskNotificator

class NotificationService : Service() {

    private var syncTaskStateLiveData: LiveData<SyncTask.State>? = null
    private var syncTaskStateObserver: Observer<SyncTask.State>? = null
    private val syncTaskNotificator2: SyncTaskNotificator2 by lazy {
        App.getAppComponent().getSyncTaskNotificator2()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra(TASK_ID)?.also { startWatchingState(it) }
            ?: Log.e(TAG, "Task id not found in Intent")
        return START_NOT_STICKY
    }

    private fun startWatchingState(taskId: String) {
        if (null == syncTaskStateLiveData) {
            syncTaskStateLiveData = App.getAppComponent().getSyncTaskStateReader().getSyncTaskStateAsLiveData(taskId)

            if (null == syncTaskStateObserver)
                syncTaskStateObserver = Observer { state ->
                    syncTaskNotificator.showNotification()
                }

            syncTaskStateLiveData?.observeForever(syncTaskStateObserver!!)
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? { TODO("Not yet implemented") }

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

//                    Bundle().apply { putString(TASK_ID, taskId) }

        fun stop(context: Context) { context.stopService(intent(context)) }

        private fun intent(context: Context): Intent = Intent(context, NotificationService::class.java)
    }
}
