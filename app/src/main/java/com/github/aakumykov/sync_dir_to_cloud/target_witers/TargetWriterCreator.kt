package com.github.aakumykov.sync_dir_to_cloud.target_witers

import com.github.aakumykov.kotlin_playground.target_writers.TargetWriter
import com.github.aakumykov.kotlin_playground.target_writers.TargetWriterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

class TargetWriterCreator @Inject constructor(
    private val factoriesMap: Map<StorageType, @JvmSuppressWildcards TargetWriterAssistedFactory>
) {
    fun createTargetWriter(storageType: StorageType, authToken: String): TargetWriter? {
        return factoriesMap[storageType]?.create(authToken)
    }
}