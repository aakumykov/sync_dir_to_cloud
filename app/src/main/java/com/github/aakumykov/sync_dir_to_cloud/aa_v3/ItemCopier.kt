package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files.SyncObjectFileCopierCreator
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.actualSize
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

// TODO: Options, откуда брать "перезаписывать"
class ItemCopier @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val syncOptions: SyncOptions,
    private val syncObjectFileCopierCreator: SyncObjectFileCopierCreator,
){
    suspend fun process(sourceObject: SyncObject?, targetObject: SyncObject?) {

        if (null == sourceObject) {
            Log.e(TAG, "sourceObject argument it null")
            return
        }
        if (null == targetObject) {
            Log.e(TAG, "targetObject argument it null")
            return
        }

        val sourceFileAbsolutePath = sourceObject.absolutePathIn(syncTask.sourcePath!!)
        val targetFileAbsolutePath = targetObject.absolutePathIn(syncTask.targetPath!!)

        val progressCalculator = ProgressCalculator(sourceObject.actualSize)

        syncObjectFileCopierCreator.createFileCopierFor(syncTask)
            ?.copyDataFromPathToPath(
                absoluteSourceFilePath = sourceFileAbsolutePath,
                absoluteTargetFilePath = targetFileAbsolutePath,
                progressCalculator = progressCalculator,
                overwriteIfExists = syncOptions.overwriteIfExists,
            ) { process ->
                Log.d(TAG, "progress: $process")
            }
    }

    companion object {
        val TAG: String = ItemCopier::class.java.simpleName
    }
}


@AssistedFactory
interface ItemCopierAssistedFactory {
    fun create(syncTask: SyncTask): ItemCopier
}