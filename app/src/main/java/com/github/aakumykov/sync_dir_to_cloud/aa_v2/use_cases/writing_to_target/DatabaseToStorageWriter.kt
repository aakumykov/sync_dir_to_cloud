package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.DeletedFilesDeleter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class DatabaseToStorageWriter @Inject constructor(
    // Каталоги
//    private val deletedDirsDeleter: DeletedDirsDeleter,
//    private val newDirsCreator: NewDirsCreator,
//    private val lostDirsCreator: LostDirsCreator,
    // Файлы
    private val deletedFilesDeleter: DeletedFilesDeleter,
//    private val newFilesCopier: NewFilesCopier,
//    private val modifiedFilesCopier: ModifiedFilesCopier,
//    private val lostFilesCopier: LostFilesCopier,
    //
    private val cloudAuthReader: CloudAuthReader,

    ) {
    /*
        I. Синхронизировать каталоги
            1. Удалить удалённые каталоги (стратегия)
            2. Создать новые каталоги (стратегия)
            3. Восстановить пропавшие каталоги (стратегия)
       II. Синхронизировать файлы
            1. Удалить удалённое (стратегия)
            2. Копировать новое (стратегия)
            3. Копировать изменившееся (стратегия)
            4. Восстановить пропавшее (стратегия)
   */

    suspend fun writeFromDatabaseToStorage(syncTask: SyncTask) {

        val sourceAuth = cloudAuthReader.getCloudAuth(syncTask.sourceAuthId!!)
        val targetAuth = cloudAuthReader.getCloudAuth(syncTask.targetAuthId!!)

        // TODO: syncTaskErrorSetter
        if (null == sourceAuth) {
            Log.e(TAG, "Source auth is null")
            return
        }

        // TODO: syncTaskErrorSetter
        if (null == targetAuth) {
            Log.e(TAG, "Target auth is null")
            return
        }

        deletedFilesDeleter.doWork(syncTask, targetAuth)
    }

    companion object {
        val TAG: String = DatabaseToStorageWriter::class.java.simpleName
    }
}
