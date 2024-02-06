package com.github.aakumykov.sync_dir_to_cloud.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

@Dao
interface SyncObjectDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(syncObject: SyncObject)

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId")
    fun getSyncObjectsForTask(taskId: String): List<SyncObject>

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId AND modification_state IN (:modificationStateList)")
    fun getNewAndChangedSyncObjectsForTask(taskId: String, modificationStateList: Array<ModificationState>): List<SyncObject>

    @Query("UPDATE sync_objects SET execution_state = :state, execution_error = :errorMsg WHERE id = :syncObjectId")
    fun setExecutionState(syncObjectId: String, state: ExecutionState, errorMsg: String)

    @Query("SELECT * FROM sync_objects WHERE task_id = :taskId")
    fun getSyncObjectList(taskId: String): LiveData<List<SyncObject>>

    @Query("DELETE FROM sync_objects WHERE task_id = :taskId")
    fun deleteObjectsForTask(taskId: String)

    @Query("UPDATE sync_objects SET sync_date = :date WHERE id = :id")
    fun setSyncDate(id: String, date: Long)


    @Query(NAME_AND_PATH_QUERY)
    fun hasObject(name: String, relativeParentDirPath: String): Boolean

    @Query(NAME_AND_PATH_QUERY)
    fun getSyncObject(name: String, relativeParentDirPath: String): SyncObject?

    @Query("UPDATE sync_objects SET modification_state = :modificationState WHERE task_id = :taskId")
    fun setStateOfAllItems(taskId: String, modificationState: ModificationState)


    companion object {
        const val NAME_AND_PATH_QUERY = "SELECT * FROM sync_objects WHERE name = :name AND relative_parent_dir_path = :relativeParentDirPath"
    }
}