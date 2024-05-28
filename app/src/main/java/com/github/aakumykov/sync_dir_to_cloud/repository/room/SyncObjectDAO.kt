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

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId AND modification_state = :modificationState")
    suspend fun getSyncObjectsForTaskWithModificationState(taskId: String, modificationState: ModificationState): List<SyncObject>

    @Query("UPDATE sync_objects SET sync_state =  :state, sync_error = :errorMsg WHERE id = :syncObjectId")
    suspend fun setExecutionState(syncObjectId: String, state: ExecutionState, errorMsg: String)

    @Deprecated("Добавить суффикс 'LiveData'")
    @Query("SELECT * FROM sync_objects WHERE storage_half = :storageHalf AND task_id = :taskId")
    fun getSyncObjectList(storageHalf: StorageHalf, taskId: String): LiveData<List<SyncObject>>

    @Query("DELETE FROM sync_objects WHERE task_id = :taskId")
    suspend fun deleteObjectsForTask(taskId: String)

    @Query("UPDATE sync_objects SET sync_date = :date WHERE id = :id")
    suspend fun setSyncDate(id: String, date: Long)

    @Query("SELECT * FROM sync_objects WHERE storage_half = :storageHalf AND task_id = :taskId AND name = :name")
    suspend fun getSyncObject(storageHalf: StorageHalf, taskId: String, name: String): SyncObject?

    @Query("DELETE FROM sync_objects WHERE task_id = :taskId AND modification_state = :modificationState AND sync_state = :syncState")
    suspend fun deleteObjectsWithModificationAndSyncState(taskId: String, modificationState: ModificationState, syncState: ExecutionState)

    @Update
    suspend fun updateSyncObject(syncObject: SyncObject)


    @Query("UPDATE sync_objects " +
            "SET modification_state = :modificationState " +
            "WHERE storage_half = :storageHalf " +
            "AND name = :name " +
            "AND relative_parent_dir_path = :relativeParentDirPath " +
            "AND task_id = :taskId")
    suspend fun changeModificationState(
        storageHalf: StorageHalf,
        name: String,
        relativeParentDirPath: String,
        taskId: String,
        modificationState: ModificationState
    )


    @Query("UPDATE sync_objects SET modification_state = :modificationState WHERE storage_half = :storageHalf AND task_id = :taskId")
    suspend fun setStateOfAllItems(storageHalf: StorageHalf, taskId: String, modificationState: ModificationState)

    @Query("SELECT * FROM sync_objects WHERE storage_half = :storageHalf AND task_id = :taskId AND sync_state = :syncState")
    suspend fun getObjectsWithSyncState(
        storageHalf: StorageHalf,
        taskId: String,
        syncState: ExecutionState
    ): List<SyncObject>

    @Query("SELECT * FROM sync_objects WHERE storage_half = :storageHalf AND task_id = :taskId AND modification_state = :modificationState")
    suspend fun getObjectsWithModificationState(
        storageHalf: StorageHalf,
        taskId: String,
        modificationState: ModificationState
    ): List<SyncObject>

    @Query("SELECT * FROM sync_objects WHERE storage_half = :storageHalf AND task_id = :taskId AND sync_state = :syncState")
    suspend fun getSyncObjectsForTaskWithSyncState(
        storageHalf: StorageHalf,
        taskId: String, syncState: ExecutionState
    ): List<SyncObject>

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId AND storage_half = :storageHalf")
    fun getObjectsForTask(taskId: String, storageHalf: StorageHalf): List<SyncObject>

    //
    // Разработал для выборки исчезнувших в приёмнике элементов, такой запрос. Но будет ли он работать?
    //
    /*@Query("SELECT * FROM sync_objects AS T2, sync_objects AS T1 " +
            "INNER JOIN sync_objects ON T1.name = T2.name " +
            "WHERE (T2.task_id = :taskId) " +
            "AND (T2.storage_half = 'TARGET' AND T1.storage_half = 'SOURCE') " +
            "AND (T2.modification_state = 'DELETED' AND T1.modification_state IS NOT 'DELETED') " +
            "GROUP BY T2.name")*/
    @Query("SELECT * " +
            "FROM sync_objects AS T2 " +
            "INNER JOIN sync_objects AS T1 " +
            "ON T1.name = T2.name " +
            "WHERE (T2.task_id = :taskId) " +
            "AND (T2.storage_half = 'TARGET' AND T1.storage_half = 'SOURCE') " +
            "AND (T2.modification_state = 'DELETED' AND T1.modification_state IS NOT 'DELETED') " +
            "GROUP BY T2.name")
    fun getObjectsNotDeletedInSourceButDeletedInTarget(taskId: String): List<SyncObject>


    @Query("UPDATE sync_objects " +
            "SET sync_error = :errorMessage " +
            "WHERE storage_half = :storageHalf " +
            "AND name = :name " +
            "AND relative_parent_dir_path = :relativeParentDirPath " +
            "AND task_id = :taskId")
    @Deprecated("переделать setExecutionState()")
    fun setErrorState(storageHalf: StorageHalf,
                      name: String,
                      relativeParentDirPath: String,
                      taskId: String,
                      errorMessage: String?)
}