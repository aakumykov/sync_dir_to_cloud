package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.backuper

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/*
Алгоритм:
10) получить relativeParentDirPath
20) [в каталоге для бекапов] создать подкаталог по этому пути
30) переместить файл/папку в созданный подкаталог
 */
class InStorageBackuper @AssistedInject constructor(
    @Assisted private val currentBackupDirName: String,
    @Assisted private val syncTask: SyncTask,
    private val cloudReaderGetter: CloudReaderGetter,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    suspend fun backupInSource(syncObject: SyncObject, sourceBackupDirName: String) {
        
//        val dirToBackupTo = sourceCloudWriter.createDirResult(syncTask.sourcePath,)
    }

    private val sourceCloudWriter: CloudWriter
        get() = cloudWriterGetter.getSourceCloudWriter(syncTask)
}