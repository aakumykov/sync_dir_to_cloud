package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

abstract class ReadingStrategy {

    abstract fun isAcceptedForSync(syncObject: SyncObject): Boolean


    /**
     * Отбирает для синхронизации новые, изменённые или никогда не синхронизировавшиеся объекты.
     */
    open class Default : ReadingStrategy() {
        override fun isAcceptedForSync(syncObject: SyncObject): Boolean {

            val isNewOrModified = syncObject.stateInStorage in arrayOf(
                StateInStorage.NEW,
                StateInStorage.MODIFIED
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