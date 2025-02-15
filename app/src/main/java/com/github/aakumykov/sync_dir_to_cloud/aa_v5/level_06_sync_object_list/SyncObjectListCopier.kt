package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_06_sync_object_list

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopier5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object.SyncObjectCopierAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.CoroutineFileOperationJob
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class SyncObjectListCopier5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val chunkSize: Int,
    @Assisted private val scope: CoroutineScope,
    @CoroutineFileOperationJob private val fileOperationJob: CompletableJob,
    private val syncObjectCopierAssistedFactory5: SyncObjectCopierAssistedFactory5,
) {
    @Throws(Exception::class)
    suspend fun copySyncObjectListFromSourceToTargetChunkByChunk(list: List<SyncObject>, overwriteIfExists: Boolean): Job {

        return scope.launch {
            list.chunked(chunkSize).forEach { listChunk ->

                copyListFromSourceToTarget(listChunk, overwriteIfExists).join()

            }.also {
                // Хак против остановки хода обработки после .joinAll()
                //  https://stackoverflow.com/questions/66003458/how-to-correctly-join-all-jobs-launched-in-a-coroutinescope
                fileOperationJob.complete()
            }
        }
    }


    @Throws(Exception::class)
    suspend fun copySyncObjectListFromTargetToSourceChunkByChank(list: List<SyncObject>, overwriteIfExists: Boolean): Job {

        return scope.launch {
            list.chunked(chunkSize).forEach { listChunk ->

                copyListFromTargetToSource(listChunk, overwriteIfExists).join()

            }.also {
                // Хак против остановки хода обработки после .joinAll()
                //  https://stackoverflow.com/questions/66003458/how-to-correctly-join-all-jobs-launched-in-a-coroutinescope
                fileOperationJob.complete()
            }
        }
    }


    private fun copyListFromSourceToTarget(list: List<SyncObject>, overwriteIfExists: Boolean): Job {
        return scope.launch {

            Log.d(TAG, "Скачиваю кусок размером ${list.size}")

            list.map { syncObject ->

                launch (fileOperationJob) {
                    syncObjectCopier.copyFromSourceToTarget(syncObject, overwriteIfExists)
                }

            }.joinAll()
        }
    }


    private fun copyListFromTargetToSource(list: List<SyncObject>, overwriteIfExists: Boolean): Job {
        return scope.launch {

            Log.d(TAG, "Скачиваю кусок размером ${list.size}")

            list.map { syncObject ->

                launch (fileOperationJob) {
                    syncObjectCopier.copyFromTargetToSource(syncObject, overwriteIfExists)
                }

            }.joinAll()
        }
    }


    private val syncObjectCopier: SyncObjectCopier5
        get() = syncObjectCopierAssistedFactory5.create(syncTask)


    companion object {
        val TAG: String = SyncObjectListCopier5::class.java.simpleName
    }
}


@AssistedFactory
interface SyncObjectListCopierAssistedFactory5 {
    fun create(syncTask: SyncTask, chunkSize: Int, scope: CoroutineScope): SyncObjectListCopier5
}