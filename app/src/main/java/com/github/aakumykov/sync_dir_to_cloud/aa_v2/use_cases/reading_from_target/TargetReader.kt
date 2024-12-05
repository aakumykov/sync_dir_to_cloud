package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.reading_from_target

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.in_target_existence_checker.InTargetExistenceChecker
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import javax.inject.Inject

class TargetReader @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val inTargetExistenceCheckerFactory: InTargetExistenceChecker.Factory,
    private val executionLogger: ExecutionLogger,
    private val resources: Resources,
) {
    suspend fun readWithCheckFromTarget(syncTask: SyncTask, executionId: String) {
        try {

            executionLogger.log(ExecutionLogItem.createStartingItem(
                syncTask.id,
                executionId,
                resources.getString(R.string.EXECUTION_LOG_reading_target)
            ))

            syncObjectReader.getAllObjectsForTask(syncTask.id).forEach { syncObject ->
                inTargetExistenceCheckerFactory
                    .create(syncTask)
                    .checkObjectExists(syncObject)
            }

            executionLogger.updateLog(ExecutionLogItem.createFinishingItem(
                syncTask.id,
                executionId,
                resources.getString(R.string.EXECUTION_LOG_reading_target)
            ))

        }  catch (e: Exception) {
            e.errorMsg.also { errorMessage ->
                Log.e(TAG, errorMessage, e)
                executionLogger.updateLog(ExecutionLogItem.createErrorItem(
                    syncTask.id,
                    executionId,
                    errorMessage,
                ))
            }
        }
    }

    companion object {
        val TAG: String = TargetReader::class.java.simpleName
    }
}