package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageHalf

@Dao
interface SyncObjectDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(syncObject: SyncObject)

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId AND modification_state IN (:modificationStateList)")
    suspend fun getSyncObjectsForTaskWithModificationStates(taskId: String, modificationStateList: Array<ModificationState>): List<SyncObject>

    @Query("UPDATE sync_objects SET execution_state =  :state, execution_error = :errorMsg WHERE id = :syncObjectId")
    suspend fun setExecutionState(syncObjectId: String, state: ExecutionState, errorMsg: String)

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId")
    fun getSyncObjectList(taskId: String): LiveData<List<SyncObject>>

    @Query("DELETE FROM sync_objects WHERE task_id = :taskId")
    suspend fun deleteObjectsForTask(taskId: String)

    @Query("UPDATE sync_objects SET sync_date = :date WHERE id = :id")
    suspend fun setSyncDate(id: String, date: Long)

    @Query("SELECT * FROM sync_objects WHERE storage_half = :storageHalf AND task_id = :taskId AND name = :name")
    suspend fun getSyncObject(storageHalf: StorageHalf, taskId: String, name: String): SyncObject?

    @Query("DELETE FROM sync_objects WHERE task_id = :taskId AND modification_state = :modificationState AND execution_state = :syncState")
    suspend fun deleteObjectsWithModificationAndSyncState(taskId: String, modificationState: ModificationState, syncState: ExecutionState)

    @Update
    suspend fun updateSyncObject(syncObject: SyncObject)

    @Query("UPDATE sync_objects SET modification_state = :modificationState WHERE id = :objectId")
    suspend fun changeModificationState(objectId: String, modificationState: ModificationState)

    @Query("UPDATE sync_objects SET modification_state = :modificationState WHERE storage_half = :storageHalf AND task_id = :taskId")
    suspend fun setStateOfAllItems(storageHalf: StorageHalf, taskId: String, modificationState: ModificationState)

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId AND execution_state = :syncState")
    suspend fun getObjectsWithSyncState(taskId: String, syncState: ExecutionState): List<SyncObject>

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId AND modification_state = :modificationState")
    suspend fun getObjectsWithModificationState(taskId: String, modificationState: ModificationState): List<SyncObject>
}