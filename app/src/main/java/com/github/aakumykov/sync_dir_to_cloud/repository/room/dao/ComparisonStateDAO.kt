package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState

@Dao
interface ComparisonStateDAO {

    @Insert
    suspend fun add(comparisonState: ComparisonState)


}