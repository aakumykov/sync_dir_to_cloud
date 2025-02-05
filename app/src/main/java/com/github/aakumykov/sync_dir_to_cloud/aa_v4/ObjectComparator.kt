package com.github.aakumykov.sync_dir_to_cloud.aa_v4

import com.github.aakumykov.sync_dir_to_cloud.aa_v4.target_object_repository.TargetObjectRepository
import javax.inject.Inject

class ObjectComparator @Inject constructor(
    private val sourceObjectRepository: SourceObjectRepository,
    private val targetObjectRepository: TargetObjectRepository,
    private val resultObjectRepository: ResultObjectRepository,
) {
}