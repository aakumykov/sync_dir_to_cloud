package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.reading_from_target

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.SyncObjectChecker
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.factories.recursive_dir_reader.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import javax.inject.Inject

class InTargetItemsChecker @Inject constructor(
    private val cloudAuthReader: CloudAuthReader, // TODO: поулчать готовую
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory, // TODO: поулчать готовую
    private val syncObjectChecker: SyncObjectChecker,
    private val syncObjectStateResetter: SyncObjectStateResetter,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncObjectUpdater: SyncObjectUpdater,
    private val executionLogger: ExecutionLogger,
    private val resources: Resources,
) {
    suspend fun checkItemsInTarget(
        taskId: String,
        executionId: String,
        pathReadingFrom: String?,
        targetStorageType: StorageType,
        cloudAuth: CloudAuth?,
    ) {

        try {
            executionLogger.addLogItem(ExecutionLogItem.createStartingItem(
                taskId,
                executionId = executionId,
                resources.getString(R.string.EXECUTION_LOG_reading_target)
            ))

            if (null == pathReadingFrom)
                throw IllegalArgumentException("path argument is null")

            if (null == cloudAuth)
                throw IllegalArgumentException("cloudAuth argument is null")

            syncObjectStateResetter.markAllObjectsAsDeleted(taskId)

            recursiveDirReaderFactory
                .create(targetStorageType, cloudAuth.authToken)!!
                .listDirRecursively(pathReadingFrom)
                .forEach { fileListItem ->
                    when (val checkResult = syncObjectChecker.check(fileListItem, taskId, pathReadingFrom)) {
                        is SyncObjectChecker.CheckResult.New -> syncObjectAdder.addSyncObject(
                            checkResult.syncObject
                        )

                        is SyncObjectChecker.CheckResult.Modified -> syncObjectUpdater.updateSyncObject(
                            checkResult.syncObject
                        )

                        is SyncObjectChecker.CheckResult.Unchanged -> {
                            Log.d(TAG, "Объект '${fileListItem.absolutePath}' не изменился.")
                        }

                        is SyncObjectChecker.CheckResult.Deleted -> {
                            Log.d(TAG, "Объект '${fileListItem.absolutePath}' удалён.")
                        }
                    }
                }

        } catch (e: Exception) {

        }
    }

    companion object {
        val TAG: String = InTargetItemsChecker::class.java.simpleName
    }
}