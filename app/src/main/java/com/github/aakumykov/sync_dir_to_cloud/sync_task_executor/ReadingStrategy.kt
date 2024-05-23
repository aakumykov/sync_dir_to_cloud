package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

abstract class ReadingStrategy {

    abstract fun isAcceptedForSync(syncObject: SyncObject): Boolean


    /**
     * Отбирает для синхронизации новые, изменённые или никогда не синхронизировавшиеся объекты.
     */
    open class Default : ReadingStrategy() {
        override fun isAcceptedForSync(syncObject: SyncObject): Boolean {

            val isNewOrModified = syncObject.modificationState in arrayOf(
                ModificationState.NEW,
                ModificationState.MODIFIED
            )

            val isNeverSynced = syncObject.syncState == ExecutionState.NEVER

            return isNewOrModified or isNeverSynced
        }
    }


    class All : Default() {
        override fun isAcceptedForSync(syncObject: SyncObject): Boolean {
            return true
        }
    }
}