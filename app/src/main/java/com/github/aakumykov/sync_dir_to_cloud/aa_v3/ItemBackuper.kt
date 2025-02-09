package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.dirs_backuper.DirsBackuper
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.dirs_backuper.DirsBackuperCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper.FilesBackuper
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper.FilesBackuperCreator
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

// TODO: создавать Бекапер 1 раз для задачи
class ItemBackuper @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val filesBackuperCreator: FilesBackuperCreator,
    private val dirsBackuperCreator: DirsBackuperCreator,
){
    suspend fun process(targetObject: SyncObject?) {
        Log.w(TAG, "БЕКАП НЕ РЕАЛИЗОВАН")
    }

    private suspend fun filesBackuper(): FilesBackuper? {
        return filesBackuperCreator.createFilesBackuperForSyncTask(syncTask, executionId)
    }

    private suspend fun dirsBackuper(): DirsBackuper? {
        return dirsBackuperCreator.createDirsBackuperForTask(syncTask, executionId)
    }

    companion object {
        val TAG: String = ItemBackuper::class.java.simpleName
    }
}

@AssistedFactory
interface ItemBackuperAssistedFactory {
    fun create(syncTask: SyncTask, executionId: String): ItemBackuper
}