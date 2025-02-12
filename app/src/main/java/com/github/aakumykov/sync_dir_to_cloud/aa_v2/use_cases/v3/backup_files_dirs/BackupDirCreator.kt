package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterLocator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@Deprecated("Переименовтаь в Maker")
class BackupDirCreator (
    private val cloudWriter: CloudWriter
) {
    /**
     * @return Полный путь к созданному каталогу, обёрнутый в [kotlin.Result].
     */
    fun createBackupDirFor(syncTask: SyncTask): Result<String> {

        val dateSuffix = SimpleDateFormat(BackupConfig.BACKUP_DIR_DATE_TIME_FORMAT, Locale.getDefault()).format(syncTask.lastStart)
        val backupDirName = "${BackupConfig.BACKUP_DIR_PREFIX}_${dateSuffix}"
        val backupParentDir = File(syncTask.targetPath!!, BackupConfig.BACKUPS_TOP_DIR_NAME).absolutePath

        return try {
            Result.success(cloudWriter.createDirResult(backupParentDir, backupDirName).getOrThrow())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

class BackupDirCreatorCreator @Inject constructor(
    private val cloudAuthReader: CloudAuthReader,
    private val cloudWriterLocator: CloudWriterLocator
) {
    suspend fun createBackupDirCreatorFor(syncTask: SyncTask): BackupDirCreator? {
        return cloudAuthReader.getCloudAuth(syncTask.targetAuthId!!)?.let { cloudAuth ->
            cloudWriterLocator.getCloudWriter(syncTask.targetStorageType, cloudAuth.authToken)?.let { cloudWriter ->
                BackupDirCreator(cloudWriter)
            }
        }
    }
}