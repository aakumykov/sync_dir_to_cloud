package com.github.aakumykov.sync_dir_to_cloud.a0_backupers.v2_ugly

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.config.BackupConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudWritersHolder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.github.aakumykov.sync_dir_to_cloud.utils.formattedDateTime
import java.io.File
import javax.inject.Inject

@Deprecated("Переименовтаь в Maker")
class BackupDirCreator (
    private val cloudWriter: CloudWriter
) {
    /**
     * @return Полный путь к созданному каталогу, обёрнутый в [kotlin.Result].
     */
    fun createBackupDirFor(syncTask: SyncTask): Result<String> {

        val dateSuffix = formattedDateTime(syncTask.lastStart ?: currentTime())
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
    private val cloudWritersHolder: CloudWritersHolder
) {
    suspend fun createBackupDirCreatorFor(syncTask: SyncTask): BackupDirCreator? {
        return cloudAuthReader.getCloudAuth(syncTask.targetAuthId!!)?.let { cloudAuth ->
            cloudWritersHolder.getCloudWriter(syncTask.targetStorageType, cloudAuth.authToken)?.let { cloudWriter ->
                BackupDirCreator(cloudWriter)
            }
        }
    }
}