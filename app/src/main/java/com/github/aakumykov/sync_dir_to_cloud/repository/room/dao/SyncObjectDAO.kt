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
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide

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
            "AND sync_side = :syncSide " +
            "AND name = :name " +
            "AND relative_parent_dir_path = :relativeParentDirPath")
    suspend fun getSyncObject(
        taskId: kotlin.String,
        syncSide: SyncSide,
        name: kotlin.String,
        relativeParentDirPath: kotlin.String
    ): SyncObject?


    @Query("DELETE FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND state_in_storage = :stateInStorage " +
            "AND sync_state = :syncState")
    suspend fun deleteObjectsWithModificationAndSyncState(
        taskId: String,
        stateInStorage: StateInStorage,
        syncState: ExecutionState
    )


    @Update
    suspend fun updateSyncObject(syncObject: SyncObject)


    @Query("UPDATE sync_objects " +
            "SET state_in_storage = :stateInStorage " +
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
            "SET just_checked = 'false' " +
            "WHERE task_id = :taskId")
    suspend fun markAllObjectsAsNotChecked(taskId: String)


    @Query("SELECT * FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND sync_state = :syncState")
    suspend fun getObjectsWithSyncState(
        taskId: String,
        syncState: ExecutionState
    ): List<SyncObject>


    @Query("SELECT * FROM sync_objects " +
            "WHERE task_id = :taskId " +
            "AND state_in_storage = :stateInStorage"
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
            "AND state_in_storage IS NOT 'DELETED'")
    fun getObjectsNotDeletedInSourceButDeletedInTarget(taskId: String): List<SyncObject>


    @Query("UPDATE sync_objects SET is_exists_in_target = :isExists WHERE id = :objectId")
    fun setExistsInTarget(objectId: String, isExists: Boolean)

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId")
    fun getAllObjectsForTask(taskId: String): List<SyncObject>

    @Query("SELECT * FROM sync_objects " +
            "WHERE sync_side = :syncSide " +
            "AND task_id = :taskId")
    fun getAllObjectsForTask(syncSide: SyncSide, taskId: String): List<SyncObject>

    @Query("DELETE FROM sync_objects WHERE id = :objectId AND state_in_storage = 'DELETED'")
    fun deleteDeletedObject(objectId: String)

    @Query("DELETE FROM sync_objects WHERE task_id = :taskId")
    fun deleteAllObjectsForTask(taskId: String)

    // TODO: разбить на отдельные методы и использовать транзакцию ...
    @Query("UPDATE sync_objects SET " +
            "state_in_storage = :stateInStorage, " +
            "just_checked = :justChecked " +
            "WHERE id = :objectId")
    fun updateStateInStorage(objectId: String, stateInStorage: StateInStorage, justChecked: Boolean)

    @Query("UPDATE sync_objects SET name = :newName WHERE id = :objectId")
    fun renameObject(objectId: String, newName: String)

    // TODO: разбить на отдельные методы и использовать транзакцию
    // TODO: ExecutionState здесь не 'NEVER', а 'сброшенный'
    //  м.б. добавить такой?
    @Query("UPDATE sync_objects SET " +
            "size = :size, " +
            "m_time = :mTime, " +
            "state_in_storage = 'MODIFIED', " +
            "sync_state = 'NEVER'," +
            "just_checked = 'true'" +
            "WHERE id = :objectId")
    fun updateAsModified(
        objectId: String,
        size: Long,
        mTime: Long
    )

    @Query("UPDATE sync_objects SET " +
            "state_in_storage = 'DELETED' " +
            "WHERE task_id = :taskId " +
            "AND just_checked = 'false'")
    fun markAllNotCheckedObjectsAsDeleted(taskId: String)

    @Query("UPDATE sync_objects SET just_checked = :value WHERE id = :objectId")
    fun setJustChecked(objectId: String, value: Boolean)

    @Query("UPDATE sync_objects SET state_in_storage = :stateInStorage WHERE id = :objectId")
    fun setStateInStorage(objectId: String, stateInStorage: StateInStorage)

    @Query("UPDATE sync_objects SET size = :size, m_time = :mTime WHERE id = :objectId")
    fun updateMetadata(objectId: String, size: Long, mTime: Long)

    @Query("DELETE FROM sync_objects WHERE " +
            "sync_state = 'SUCCESS' " +
            "AND state_in_storage = 'DELETED' " +
            "AND task_id = :taskId")
    fun deleteProcessedObjectsWithDeletedState(taskId: String)
}