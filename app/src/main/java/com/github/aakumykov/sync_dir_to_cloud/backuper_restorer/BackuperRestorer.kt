package com.github.aakumykov.sync_dir_to_cloud.backuper_restorer

import android.content.Context
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SchedulingSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncTaskRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import javax.inject.Inject


class BackuperRestorer @Inject constructor(
    @AppContext private val appContext: Context,
    private val syncTaskRepository: SyncTaskRepository,
    private val syncTaskManagingUseCase: SyncTaskManagingUseCase,
    private val syncTaskSchedulingUseCase: SchedulingSyncTaskUseCase,
) {
    init {
        backupDir.mkdir()
    }

    suspend fun backupTasks() {
        syncTaskRepository.getAllTasks().also { allTasks ->
            writeToBackupFile(gson.toJson(allTasks))
            allTasks.forEach {
                syncTaskManagingUseCase.deleteSyncTask(it)
                syncTaskSchedulingUseCase.unScheduleSyncTask(it)
            }
        }
    }

    suspend fun restoreTasks() {
        readFromBackupFile().also { json ->
            val type = object : TypeToken<List<SyncTask>>() {}.type
            val list = gson.fromJson<List<SyncTask>>(json, type)
            list.forEach { syncTask ->
                syncTaskRepository.createSyncTask(syncTask)
            }
        }
    }

   private fun writeToBackupFile(data: String) {
       backupFile.writeBytes(data.toByteArray())
   }

    private fun readFromBackupFile(): String {
        return backupFile.readText()
    }

    private val backupDir: File
        get() = File(appContext.cacheDir, "backup")

    private val backupFile: File
        get() = File(backupDir, "sync_tasks.json")

    private val gson: Gson by lazy { Gson() }
}