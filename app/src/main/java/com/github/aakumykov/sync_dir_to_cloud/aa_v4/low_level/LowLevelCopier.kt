package com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level

import com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.very_basic.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.very_basic.CloudWriterGetter
import javax.inject.Inject

class LowLevelCopier @Inject constructor(
    private val cloudReaderGetter: CloudReaderGetter,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    suspend fun copyFromSourceToTarget(
        sourcePath: String,
        targetPath: String
    ) {

    }
}