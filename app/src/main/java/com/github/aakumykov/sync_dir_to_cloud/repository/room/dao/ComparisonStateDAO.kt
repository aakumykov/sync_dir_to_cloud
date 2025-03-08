package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState

@Dao
interface ComparisonStateDAO {

    @Insert
    suspend fun add(comparisonState: ComparisonState)

    @Query("SELECT * FROM comparison_states " +
            "WHERE task_id = :taskId " +
            "AND execution_id = :executionId")
    suspend fun getAllFor(taskId: String, executionId: String): List<ComparisonState>

    @Query("DELETE FROM comparison_states WHERE task_id = :taskId")
    suspend fun deleteAllFor(taskId: String)
}