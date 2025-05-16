package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file

import androidx.core.util.Supplier
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class UniqueDirNameMaker @AssistedInject constructor(
    @Assisted("prefix") private val dirNamePrefixSupplier: Supplier<String>,
    @Assisted("suffix") private val dirNameSuffixSupplier: Supplier<String>,
    @Assisted private val maxCreationAttemptsCount: Int,
) {
    private var attemptCount = 1

    @Throws(RuntimeException::class)
    fun getUniqueDirName(): String {

        val dirName = "${dirNamePrefixSupplier.get()}_${dirNameSuffixSupplier.get()}"

        if (attemptCount++ > maxCreationAttemptsCount)
            throw RuntimeException("Maximum dir creation attempts count ($maxCreationAttemptsCount) exceeded.")

        return dirName
    }
}


@AssistedFactory
interface UniqueDirNameMakerAssistedFactory {
    fun create(
        @Assisted("prefix") dirNamePrefixSupplier: Supplier<String>,
        @Assisted("suffix") dirNameSuffixSupplier: Supplier<String>,
        maxCreationAttemptsCount: Int
    ): UniqueDirNameMaker
}