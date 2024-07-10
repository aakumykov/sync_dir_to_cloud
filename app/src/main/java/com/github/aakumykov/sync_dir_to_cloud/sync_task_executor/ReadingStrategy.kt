package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInSource
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

abstract class ReadingStrategy {

    abstract fun isAcceptedForSync(syncObject: SyncObject): Boolean


    /**
     * Отбирает для синхронизации новые, изменённые или никогда не синхронизировавшиеся объекты.
     */
    open class Default : ReadingStrategy() {
        override fun isAcceptedForSync(syncObject: SyncObject): Boolean {

            val isNewOrModified = syncObject.stateInSource in arrayOf(
                StateInSource.NEW,
                StateInSource.MODIFIED
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

    companion object {
        val DEFAULT: ReadingStrategy = ReadingStrategy.Default()
    }
}