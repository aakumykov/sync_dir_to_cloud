package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import androidx.annotation.CallSuper
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.isNew

abstract class ReadingStrategy(
    private val syncObjectFileFilteringPredicate: (SyncObject) -> Boolean,
    private val syncObjectDirFilteringPredicate: (SyncObject) -> Boolean,
) {

    constructor(syncObjectUnifiedFilteringPredicate: (SyncObject) -> Boolean)
            : this(syncObjectUnifiedFilteringPredicate, syncObjectUnifiedFilteringPredicate)

    fun isAcceptedForSync(syncObject: SyncObject): Boolean {
        return when(syncObject.isDir) {
            true -> syncObjectDirFilteringPredicate.invoke(syncObject)
            false -> syncObjectFileFilteringPredicate.invoke(syncObject)
        }
    }

    companion object {
        fun createFor(syncTask: SyncTask): ReadingStrategy {
            return All()
        }
    }

    class All : ReadingStrategy({ true }, { true })

    class New : ReadingStrategy({ it.modificationState.isNew() })

    class Modified : ReadingStrategy({ it.modificationState.isNew() })

    class NewAndModified : ReadingStrategy({ it.modificationState.isNew() })
}
