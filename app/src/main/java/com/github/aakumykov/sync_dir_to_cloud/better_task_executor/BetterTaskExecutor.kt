package com.github.aakumykov.sync_dir_to_cloud.better_task_executor

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.backuper.BetterBackuperCreator
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_dir_creator.BetterDirCreatorCreator
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_file_copier.BetterFileCopierCreator
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.exceptions.TaskExecutionException
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.source_reader.BetterSourceReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import javax.inject.Inject

class BetterTaskExecutor @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val sourceReaderCreator: BetterSourceReaderCreator,
    private val backuperCreator: BetterBackuperCreator,
    private val dirCreatorCreator: BetterDirCreatorCreator,
    private val fileCopierCreator: BetterFileCopierCreator,
) {
    suspend fun executeSyncTask(taskId: String) {

        try {
            //
            // Получаю здесь SyncTask и передаю его остальным участниками,
            // чтобы все работали с идентинчым экземпляром, а не получали
            // его из БД по ходу выполнения задачи, когда он уже мог измениться.
            //
            val syncTask = syncTaskReader.getSyncTask(taskId)
            if (null == syncTask) {
                throw TaskExecutionException.CriticalException.TaskNotFoundException(taskId)
            }

            // прочитать источник
            sourceReaderCreator.createSourceReader(syncTask).readSource()

            // забекапить изменённое/удалённое
            backuperCreator.createBackuper(syncTask).backupItems()

            // создать каталоги
            dirCreatorCreator.create(syncTask).createDirs()

            // скопировать файлы
            fileCopierCreator.create(syncTask).copyFiles()
        }
        catch (e: TaskExecutionException.NonCriticalException) {
            // TODO: регистрировать здесь или в классе, который это делает?
            Log.e(TAG, e.errorMsg)
        }
    }

    companion object {
        val TAG: String = BetterTaskExecutor::class.java.simpleName
    }
}