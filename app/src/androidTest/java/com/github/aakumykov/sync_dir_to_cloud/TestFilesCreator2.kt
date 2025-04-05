package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.parent_child.TestDbStuff
import java.io.File
import kotlin.random.Random

class TestFilesCreator2 /*@AssistedInject*/ constructor(
    /*@Assisted*/ private val dbStuff: TestDbStuff
) {
    private suspend fun syncTask(): SyncTask = dbStuff.syncTaskDAO.get(TestTaskConfig.ID)

    suspend fun createFileInSource(fileName: String, sizeKb: Int = 1024): File {
        return File(syncTask().sourcePath!!, fileName).apply {
            createNewFile()
            writeBytes(Random.nextBytes(sizeKb))
        }
    }

    suspend fun createDirInSource(fileName: String, sizeKb: Int = 1024): File {
        return File(syncTask().sourcePath!!, fileName).apply {
            mkdir()
        }
    }

    suspend fun createFileInTarget(fileName: String, sizeKb: Int = 1024): File {
        return File(syncTask().sourcePath!!, fileName).apply {
            createNewFile()
            writeBytes(Random.nextBytes(sizeKb))
        }
    }

    suspend fun createDirInTarget(fileName: String, sizeKb: Int = 1024): File {
        return File(syncTask().sourcePath!!, fileName).apply {
            mkdir()
        }
    }

    /*fun randomFileName(prefix: String = "file"): String {
        return "${prefix}-${randomString}"
    }

    private val randomString: String
        get() = randomUUID.split("-").first()*/
}


/*@AssistedFactory
interface TestFilesCreator2AssistedFactory {
    fun create(syncTask: SyncTask): TestFilesCreator2
}*/
