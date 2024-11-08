package com.github.aakumykov.sync_dir_to_cloud.better_task_executor

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_backuper.BetterBackuperCreator
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_dir_maker.BetterDirMakerCreator
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_file_copier.BetterFileCopierCreator
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_target_reader.BetterTargetReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.exceptions.TaskExecutionException
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.beter_source_reader.BetterSourceReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.random.Random

class BetterTaskExecutor @Inject constructor(
    private val syncTaskReader: SyncTaskReader,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val sourceReaderCreator: BetterSourceReaderCreator,
    private val targetReaderCreator: BetterTargetReaderCreator,
    private val backuperCreator: BetterBackuperCreator,
    private val dirCreatorCreator: BetterDirMakerCreator,
    private val fileCopierCreator: BetterFileCopierCreator,
) {
    suspend fun executeSyncTask(taskId: String) {

        try {
            // установка состояния "выполняется"
            syncTaskStateChanger.setRunningState(taskId)

            //
            // Получаю здесь SyncTask и передаю его остальным участниками,
            // чтобы все работали с идентинчым экземпляром, а не получали
            // его из БД по ходу выполнения задачи, когда он уже мог измениться.
            //
            val syncTask = syncTaskReader.getSyncTask(taskId)
            if (null == syncTask)
                throw TaskExecutionException.CriticalException.TaskNotFoundException(taskId)

            delay(Random.nextLong(3000,5000))

            // прочитать источник
            sourceReaderCreator.createSourceReader(syncTask).readSource()

            // прочитать приёмник
            targetReaderCreator.createTargetReader(syncTask).readTarget()

            // забекапить изменённое/удалённое
            backuperCreator.createBackuper(syncTask).backupItems()

            // создать каталоги
            dirCreatorCreator.create(syncTask).createDirs()

            // скопировать файлы
            fileCopierCreator.create(syncTask).copyFiles()

            // установка состояния "успешно выполнено"
            syncTaskStateChanger.setSuccessState(taskId)
        }
        catch (e: TaskExecutionException.NonCriticalException) {
            // TODO: регистрировать здесь или в классе, который это делает?
            Log.e(TAG, e.errorMsg)
        }
        catch (e: Exception) {
            // установка состояния "успешно выполнено"
            syncTaskStateChanger.setErrorState(taskId, e)

            // Выбрасываю исключение, чтобы оно ушло Worker-у
            throw e
        }
    }

    companion object {
        val TAG: String = BetterTaskExecutor::class.java.simpleName
    }
}