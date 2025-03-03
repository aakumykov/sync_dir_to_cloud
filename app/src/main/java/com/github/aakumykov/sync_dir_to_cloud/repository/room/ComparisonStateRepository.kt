package com.github.aakumykov.sync_dir_to_cloud.repository.room

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ComparisonStateDAO
import javax.inject.Inject

class ComparisonStateRepository @Inject constructor(
    private val comparisonStateDAO: ComparisonStateDAO
) {
    suspend fun add(comparisonState: ComparisonState) {
        comparisonStateDAO.add(comparisonState)
    }
}