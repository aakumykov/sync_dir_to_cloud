package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.Side

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


    @Query("SELECT * FROM sync_objects WHERE id = :objectId")
    suspend fun getSyncObject(objectId: String): SyncObject?


    @Query("SELECT * FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND side = :side " +
            "AND name = :name " +
            "AND relative_parent_dir_path = :relativeParentDirPath")
    suspend fun getSyncObject(
        taskId: kotlin.String,
        side: Side,
        name: kotlin.String,
        relativeParentDirPath: kotlin.String
    ): SyncObject?


    @Query("DELETE FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND state_in_source = :stateInStorage " +
            "AND sync_state = :syncState")
    suspend fun deleteObjectsWithModificationAndSyncState(
        taskId: String,
        stateInStorage: StateInStorage,
        syncState: ExecutionState
    )


    @Update
    suspend fun updateSyncObject(syncObject: SyncObject)


    @Query("UPDATE sync_objects " +
            "SET state_in_source = :stateInStorage " +
            "AND name = :name " +
            "AND relative_parent_dir_path = :relativeParentDirPath " +
            "AND task_id = :taskId")
    suspend fun changeModificationState(
        name: String,
        relativeParentDirPath: String,
        taskId: String,
        stateInStorage: StateInStorage
    )


    @Query("UPDATE sync_objects " +
            "SET state_in_source = 'DELETED' " +
            "WHERE task_id = :taskId")
    suspend fun markAllObjectsAsDeleted(taskId: String)


    @Query("SELECT * FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND sync_state = :syncState")
    suspend fun getObjectsWithSyncState(
        taskId: String,
        syncState: ExecutionState
    ): List<SyncObject>


    @Query("SELECT * FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND state_in_source = :stateInStorage"
    )
    suspend fun getObjectsWithModificationState(
        taskId: String,
        stateInStorage: StateInStorage
    ): List<SyncObject>


    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId")
    fun getObjectsForTask(taskId: String): List<SyncObject>


    @Query("SELECT * FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND is_exists_in_target = 0 " +
            "AND state_in_source IS NOT 'DELETED'")
    fun getObjectsNotDeletedInSourceButDeletedInTarget(taskId: String): List<SyncObject>


    @Query("UPDATE sync_objects SET is_exists_in_target = :isExists WHERE id = :objectId")
    fun setExistsInTarget(objectId: String, isExists: Boolean)

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId")
    fun getAllObjectsForTask(taskId: String): List<SyncObject>

    @Query("SELECT * FROM sync_objects " +
            "WHERE side = :side " +
            "AND task_id = :taskId")
    fun getAllObjectsForTask(side: Side, taskId: String): List<SyncObject>

    @Query("DELETE FROM sync_objects WHERE id = :objectId AND state_in_source = 'DELETED'")
    fun deleteDeletedObject(objectId: String)

    @Query("DELETE FROM sync_objects WHERE task_id = :taskId")
    fun deleteAllObjectsForTask(taskId: String)

    @Query("UPDATE sync_objects SET state_in_source = :stateInStorage WHERE id = :objectId")
    fun setStateInStorage(objectId: String, stateInStorage: StateInStorage)
}