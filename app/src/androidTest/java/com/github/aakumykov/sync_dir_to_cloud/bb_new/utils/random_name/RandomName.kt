package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.local_file_helper.tests.LocalFileHelperContentsTest.Companion.DEEP_DIR_MAX_DEPTH
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.local_file_helper.tests.LocalFileHelperContentsTest.Companion.DEEP_DIR_MIN_DEPTH
import java.util.UUID
import kotlin.random.Random

val randomName: String
    get() = UUID.randomUUID().toString()


fun randomDeepDirName(minDepth: Int = DEEP_DIR_MIN_DEPTH, maxDepth: Int = DEEP_DIR_MAX_DEPTH): String {
    return buildList {
        repeat(Random.nextInt(minDepth, maxDepth+1)) {
            add(randomName)
        }
    }.joinToString(CloudWriter.DS)
}


val randomDeepDirName: String
    get() = randomDeepDirName()