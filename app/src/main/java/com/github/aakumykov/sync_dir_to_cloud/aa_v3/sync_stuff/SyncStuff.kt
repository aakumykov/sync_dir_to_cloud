package com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncObjectLoggerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.operation_logger.OperationLogger
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.operation_logger.OperationLoggerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudReaderLocator
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterLocator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class SyncStuff @Inject constructor(
    private val cloudAuthReader: CloudAuthReader,
    private val cloudReaderLocator: CloudReaderLocator,
    private val cloudWriterLocator: CloudWriterLocator,
    private val backupDirNamer: BackupDirNamer,
    private val syncObjectLoggerFactory: SyncObjectLoggerAssistedFactory,
    private val operationLoggerAssistedFactory: OperationLoggerAssistedFactory,
) {
    private var _cloudReader: CloudReader? = null
    private var _cloudWriter: CloudWriter? = null
    private var _backupDirSpec: BackupDirSpec? = null
    private var _operationLogger: OperationLogger? = null

    val cloudReader: CloudReader get() = _cloudReader!!
    val cloudWriter: CloudWriter get() = _cloudWriter!!
    val backupDirSpec: BackupDirSpec get() = _backupDirSpec!!
    val operationLogger: OperationLogger get() = _operationLogger!!


    suspend fun prepareFor(syncTask: SyncTask, executionId: String): SyncStuff {

        val sourceAuth = cloudAuthReader.getCloudAuth(syncTask.sourceAuthId)
        val targetAuth = cloudAuthReader.getCloudAuth(syncTask.targetAuthId)

        _cloudReader = cloudReaderLocator.getCloudReader(syncTask.sourceStorageType, sourceAuth?.authToken)
        _cloudWriter = cloudWriterLocator.getCloudWriter(syncTask.targetStorageType, targetAuth?.authToken)
        _backupDirSpec = backupDirNamer.createBackupDirSpec(syncTask)
        _operationLogger = operationLoggerAssistedFactory.create(syncObjectLoggerFactory.create(syncTask.id, executionId))

        return this
    }
}