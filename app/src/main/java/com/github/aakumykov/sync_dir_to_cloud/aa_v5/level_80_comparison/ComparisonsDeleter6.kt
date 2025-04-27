package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_80_comparison

import com.github.aakumykov.sync_dir_to_cloud.repository.ComparisonStateRepository
import javax.inject.Inject

class ComparisonsDeleter6 @Inject constructor(
    private val comparisonStateRepository: ComparisonStateRepository
) {
    suspend fun deleteAllFor(taskId: String) {
        comparisonStateRepository.deleteAllFor(taskId)
    }
}