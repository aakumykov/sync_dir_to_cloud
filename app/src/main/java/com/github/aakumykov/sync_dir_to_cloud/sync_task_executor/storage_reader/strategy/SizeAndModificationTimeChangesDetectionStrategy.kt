package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy

import android.util.Log
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import javax.inject.Inject

class SizeAndModificationTimeChangesDetectionStrategy @Inject constructor() : ChangesDetectionStrategy() {

    override suspend fun detectItemModification(
        sourcePath: String,
        newFsItem: FSItem,
        existingSyncObject: SyncObject
    ): StateInStorage {

        if (existingSyncObject.isDir) {
            Log.d(TAG, "каталог (принимается неизменным): ${existingSyncObject.name}")
            return StateInStorage.UNCHANGED
        }

        // FIXME: логическая проблема: сравниваются объекты разных классов: SyncObject и FSItem
        if (existingSyncObject.size == newFsItem.size) {
//                Log.d(TAG, "время изменения совпадает у ${existingSyncObject.name}")

            if (existingSyncObject.mTime == newFsItem.mTime) {
//                Log.d(TAG, "размер совпадает у ${existingSyncObject.name}")
                return StateInStorage.UNCHANGED
            } else {
                Log.d(TAG, "время изменения отличается у ${existingSyncObject.name}")
                Log.d(TAG, "   ${existingSyncObject.mTime}")
                Log.d(TAG, "   ${newFsItem.mTime}")
            }
        } else {
            Log.d(TAG, "размер отличается у ${existingSyncObject.name}")
            Log.d(TAG, "   ${existingSyncObject.size}")
            Log.d(TAG, "   ${newFsItem.size}")
        }

        return StateInStorage.MODIFIED
    }

    companion object {
        val TAG: String = SizeAndModificationTimeChangesDetectionStrategy::class.java.simpleName
    }
}