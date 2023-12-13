package com.github.aakumykov.sync_dir_to_cloud.notificator

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.github.aakumykov.sync_dir_to_cloud.utils.Logger

class SyncTaskExecutionService : Service() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_START_WORK -> startWork()
                ACTION_STOP_WORK -> stopWork()
                else -> {
                    Logger.e(TAG, "Неизвестный Intent action: ${it.action}")
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun startWork() {
        Toast.makeText(this, "startWork()", Toast.LENGTH_SHORT).show()
    }

    private fun stopWork() {
        Toast.makeText(this, "stopWork()", Toast.LENGTH_SHORT).show()
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? { TODO("Not yet implemented") }

    companion object {
        val TAG: String = SyncTaskExecutionService::class.java.simpleName

        const val ACTION_START_WORK: String = "START_WORK"
        const val ACTION_STOP_WORK: String = "STOP_WORK"

        const val CODE_START_WORK: Int = 200
        const val CODE_STOP_WORK: Int = 200
    }


}