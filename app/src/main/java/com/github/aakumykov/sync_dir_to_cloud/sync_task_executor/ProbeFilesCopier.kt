package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.SyncObjectCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isSource
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor.Companion.TAG
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProbeFilesCopier @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectCopierAssistedFactory: SyncObjectCopierAssistedFactory,
) {
    suspend fun copyFiles(syncTask: SyncTask, scope: CoroutineScope): Job {

        val syncObjectCopier = syncObjectCopierAssistedFactory.create(syncTask)

        return scope.launch {
            try {
                /*syncObjectReader
                    .getAllObjectsForTask(syncTask.id)
                    .filter { it.isSource }
                    .forEach { syncObject ->
                        debugLogSyncObject(syncObject)
                        syncObjectCopier.copyObjectFromSourceToTarget(syncObject) { p ->
                            Log.d(TAG, p.toString())
                        }
                    }*/

                listOf(1,2,3,4,5).forEach {
                    qwertyDelay(3)
                }

            } catch (e: CancellationException) {
                Log.e(TAG, e.message, e)
            }
        }
    }

    private suspend fun qwertyDelay(timeoutSec: Int) {
        cancellableDelay(timeoutSec)
    }

    private suspend fun simpleDelay(timeoutSec: Int) {
        return suspendCoroutine {  continuation ->
            thread {
                repeat(timeoutSec) {
                    TimeUnit.SECONDS.sleep(1)
                }
                continuation.resume(Unit)
            }
        }
    }

    private suspend fun cancellableDelay(timeoutSec: Int) {
        return suspendCancellableCoroutine {  continuation ->
            thread {
                repeat(timeoutSec) { i ->
                    if (!continuation.isActive) {
                        return@repeat
                    }
                    Log.d(TAG, "delay: $i")
                    TimeUnit.SECONDS.sleep(1)
                }
                continuation.resume(Unit)
            }
        }
    }

    private fun debugLogSyncObject(syncObject: SyncObject) {
        Log.d(TAG,
            if (syncObject.isDir) "Создаётся каталог '${syncObject.name}'"
            else "Копируется файл '${syncObject.name}'"
        )
    }
}