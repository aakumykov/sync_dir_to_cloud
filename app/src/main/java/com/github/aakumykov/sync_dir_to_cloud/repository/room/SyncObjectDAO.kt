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

@Dao
interface SyncObjectDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(syncObject: SyncObject)


    @Query("UPDATE sync_objects SET sync_state = :state, sync_error = :errorMsg WHERE id = :syncObjectId")
    suspend fun setSyncState(syncObjectId: String, state: ExecutionState, errorMsg: String)


    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId")
    fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>


    @Query("DELETE FROM sync_objects WHERE task_id = :taskId")
    suspend fun deleteObjectsForTask(taskId: String)


    @Query("UPDATE sync_objects SET sync_date = :date WHERE id = :objectId")
    suspend fun setSyncDate(objectId: String, date: Long)


    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId AND name = :name")
    suspend fun getSyncObject(taskId: String, name: String): SyncObject?


    @Query("DELETE FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND modification_state = :modificationState " +
            "AND sync_state = :syncState")
    suspend fun deleteObjectsWithModificationAndSyncState(
        taskId: String,
        modificationState: ModificationState,
        syncState: ExecutionState
    )


    @Update
    suspend fun updateSyncObject(syncObject: SyncObject)


    @Query("UPDATE sync_objects " +
            "SET modification_state = :modificationState " +
            "AND name = :name " +
            "AND relative_parent_dir_path = :relativeParentDirPath " +
            "AND task_id = :taskId")
    suspend fun changeModificationState(
        name: String,
        relativeParentDirPath: String,
        taskId: String,
        modificationState: ModificationState
    )


    @Query("UPDATE sync_objects " +
            "SET modification_state = :modificationState " +
            "AND task_id = :taskId")
    suspend fun setStateOfAllItems(taskId: String, modificationState: ModificationState)


    @Query("SELECT * FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND sync_state = :syncState")
    suspend fun getObjectsWithSyncState(
        taskId: String,
        syncState: ExecutionState
    ): List<SyncObject>


    @Query("SELECT * FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND modification_state = :modificationState")
    suspend fun getObjectsWithModificationState(
        taskId: String,
        modificationState: ModificationState
    ): List<SyncObject>


    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId")
    fun getObjectsForTask(taskId: String): List<SyncObject>


    @Query("SELECT * FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND is_exists_in_target = 0 " +
            "AND modification_state IS NOT 'DELETED'")
    fun getObjectsNotDeletedInSourceButDeletedInTarget(taskId: String): List<SyncObject>


    @Query("UPDATE sync_objects SET is_exists_in_target = :isExists WHERE id = :objectId")
    fun setExistsInTarget(objectId: String, isExists: Boolean)

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId")
    fun getAllObjectsForTask(taskId: String): List<SyncObject>
}