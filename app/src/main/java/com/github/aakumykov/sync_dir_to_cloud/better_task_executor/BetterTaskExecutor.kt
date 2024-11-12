package com.github.aakumykov.sync_dir_to_cloud.better_task_executor

import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.bas_state_resetter.BadStatesResetter
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.beter_source_reader.BetterSourceReaderAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_backuper.BetterBackuperAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_dir_maker.BetterDirMakerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_file_copier.BetterFileCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_file_deleter.BetterFileDeleterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.better_target_reader.BetterTargetReaderAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.dir_deleter_creator.BetterDirDeleterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.exceptions.SyncObjectError
import com.github.aakumykov.sync_dir_to_cloud.better_task_executor.exceptions.TaskExecutionException
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_object_logger.SyncObjectLogger2
import com.github.aakumykov.sync_dir_to_cloud.sync_task_logger.ExecutionLogger
import javax.inject.Inject

class BetterTaskExecutor @Inject constructor(
    private val syncTaskReader: SyncTaskReader,

    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val executionLogger: ExecutionLogger,
    private val syncObjectLogger: SyncObjectLogger2,

    private val badStateResetter: BadStatesResetter,

    private val sourceReaderFactory: BetterSourceReaderAssistedFactory,
    private val targetReaderFactory: BetterTargetReaderAssistedFactory,

    private val backuperFactory: BetterBackuperAssistedFactory,

    private val fileDeleterCreator: BetterFileDeleterAssistedFactory,
    private val dirDeleterCreator: BetterDirDeleterAssistedFactory,

    private val dirCreatorFactory: BetterDirMakerAssistedFactory,
    private val fileCopierFactory: BetterFileCopierAssistedFactory,
) {
    //
    // Код, чтобы отличать один запуск от другого.
    // Задание одно, эпизодов его запуска много.
    //
    private val executionId: String = hashCode().toString()

    //
    // Оформлено именно так, чтобы бросало исключения, если забыл
    // инициализировать.
    //
    private var _currentTaskId: String? = null
    private val taskId: String get() = _currentTaskId!!


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


            // сбросить ошибочные статусы, оставшиеся при аварийном завершении программы
            logExecutionState(R.string.SYNC_OBJECT_LOGGER_resetting_bad_sates)
            badStateResetter.resetBadStates(syncTask)


            // прочитать источник
            logExecutionState(R.string.SYNC_OBJECT_LOGGER_reading_source)
            sourceReaderFactory.create(syncTask).readSourceFilesState()


            // прочитать приёмник
            logExecutionState(R.string.SYNC_OBJECT_LOGGER_reading_target)
            targetReaderFactory.create(syncTask).readTargetFilesState()


            // забекапить изменённое/удалённое
            logExecutionState(R.string.SYNC_OBJECT_LOGGER_backuping_modified_deleted)
            backuperFactory.create(syncTask).backupItems()


            // удалить удалённые файлы (делать это перед удалением каталогов!)
            logExecutionState(R.string.SYNC_OBJECT_LOGGER_deleting_file)
            fileDeleterCreator.create(syncTask).deleteDeletedFiles()

            // удалить удалённые каталоги (делать это после удалением файлов!)
            dirDeleterCreator.create(syncTask).deleteDeletedDirs()


            // создать каталоги
            dirCreatorFactory.create(syncTask).createNewDirs()
            dirCreatorFactory.create(syncTask).createNeverSyncedDirs()
            dirCreatorFactory.create(syncTask).createLostDirsAgain()


            // скопировать файлы
            logExecutionState(R.string.SYNC_OBJECT_LOGGER_copy_new_file)
            fileCopierFactory.create(syncTask).copyFiles()

            logExecutionState(R.string.SYNC_OBJECT_LOGGER_copy_modified_file)
//            copyModifiedFiles(syncTask)

            logExecutionState(R.string.SYNC_OBJECT_LOGGER_copy_previously_forgotten_file)
//            copyPreviouslyForgottenFiles(syncTask)

            logExecutionState(R.string.SYNC_OBJECT_LOGGER_copy_in_target_lost_file)
//            copyLostFilesAgain(syncTask )


            // установка состояния "успешно выполнено"
            syncTaskStateChanger.setSuccessState(taskId)
        }
        catch (e: Exception) {
            when(e) {
                is SyncObjectError -> {
                    syncObjectLogger.logFail(e.syncObject, e.operationName, e.errorMsg)
                }
                is TaskExecutionException.NonCriticalException -> {
                    executionLogger.log(ExecutionLogItem(taskId, executionId, ExecutionLogItem.EntryType.ERROR, e.errorMsg))
                }
                else -> {
                    syncTaskStateChanger.setErrorState(taskId, e)
                    throw e
                }
            }
        }
    }

    private fun logExecutionState(@StringRes stringRes: Int) {
//        executionLogger.log()
    }


    companion object {
        val TAG: String = BetterTaskExecutor::class.java.simpleName
    }
}