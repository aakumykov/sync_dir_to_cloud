package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class BackupDirCreator constructor(
    private val cloudWriter: CloudWriter
) {
    /**
     * @return Полный путь к созданному каталогу, обёрнутый в [kotlin.Result].
     */
    fun createBackupDirFor(syncTask: SyncTask): Result<String> {

        val dateSuffix = SimpleDateFormat(BackupConfig.BACKUP_DIR_DATE_TIME_FORMAT, Locale.getDefault()).format(syncTask.lastStart)
        val backupDirName = "${BackupConfig.BACKUP_DIR_PREFIX}_${dateSuffix}"

        return try {
            cloudWriter.createDir(syncTask.targetPath!!, backupDirName)
            val fullDirPath = File(syncTask.targetPath!!, backupDirName)
            Result.success(fullDirPath.absolutePath)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

class BackupDirCreatorCreator @Inject constructor(
    private val cloudAuthReader: CloudAuthReader,
    private val cloudWriterCreator: CloudWriterCreator
) {
    suspend fun createBackupDirCreatorFor(syncTask: SyncTask): BackupDirCreator? {
        return cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.let { cloudAuth ->
            cloudWriterCreator.createCloudWriter(syncTask.targetStorageType, cloudAuth.authToken)?.let { cloudWriter ->
                BackupDirCreator(cloudWriter)
            }
        }
    }
}