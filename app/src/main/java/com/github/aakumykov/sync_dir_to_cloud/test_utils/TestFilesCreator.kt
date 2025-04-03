package com.github.aakumykov.sync_dir_to_cloud.test_utils

import android.content.Context
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

class TestFilesCreator @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
) {
    suspend fun createFileInSource(taskId: String) {
        syncTaskReader.getSyncTask(taskId).also {
            createFileIn(it.sourcePath!!)
        }
    }

    suspend fun createFileInTarget(taskId: String) {
        syncTaskReader.getSyncTask(taskId).also {
            createFileIn(it.targetPath!!)
        }
    }

    private suspend fun createFileIn(absolutePath: String) {
        File(absolutePath, randomName).createNewFile()
    }

    private val randomName: String
        get() = "file-${randomString}.txt"

    private val randomString: String
        get() = randomUUID.split("-").first()
}