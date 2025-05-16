package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_70_sync_task

import androidx.core.util.Supplier
import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ExecutionUniqueDirNameMaker @AssistedInject constructor(
    @Assisted syncTask: SyncTask,
    @Assisted("prefix") private val dirNamePrefixSupplier: Supplier<String>,
    @Assisted("suffix") private val dirNameSuffixSupplier: Supplier<String>,
    @Assisted private val maxCreationAttemptsCount: Int,
    private val cloudReaderGetter: CloudReaderGetter,
) {
    private var attemptCount = 0
    private var appendixNumber = 0

    @Throws(RuntimeException::class)
    fun getUniqueDirName(): String {

        if (attemptCount++ > maxCreationAttemptsCount)
            throw RuntimeException("Maximum dir creation attempts count ($maxCreationAttemptsCount) exceeded.")

        val dirName = "${dirNamePrefixSupplier.get()}_${dirNameSuffixSupplier.get()}${appendix}"

        return dirName
    }

    private val appendix: String get() {
        return if (0 == appendixNumber) ""
        else "_${appendixNumber++}"
    }

    private val sourceCloudReader: CloudReader by lazy {
        cloudReaderGetter.getSourceCloudReaderFor(syncTask)
    }

    private val targetCloudReader: CloudReader by lazy {
        cloudReaderGetter.getTargetCloudReaderFor(syncTask)
    }
}


@AssistedFactory
interface ExecutionUniqueDirNameMakerAssistedFactory {
    fun create(
        syncTask: SyncTask,
        @Assisted("prefix") dirNamePrefixSupplier: Supplier<String>,
        @Assisted("suffix") dirNameSuffixSupplier: Supplier<String>,
        maxCreationAttemptsCount: Int
    ): ExecutionUniqueDirNameMaker
}