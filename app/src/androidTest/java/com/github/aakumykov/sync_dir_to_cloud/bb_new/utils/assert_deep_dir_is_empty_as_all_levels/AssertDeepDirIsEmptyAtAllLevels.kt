package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.assert_deep_dir_is_empty_as_all_levels

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_children_count.childrenCount
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import org.junit.Assert
import java.io.File

fun assertDeepDirIsEmptyAtAllLevels(baseDir: File, deepDirName: String) {
    deepDirName
        .split(CloudWriter.DS)
        .reduce { acc, string ->
            val subPath = acc + CloudWriter.DS + string
            Assert.assertEquals(1, File(baseDir, subPath).childrenCount)
            subPath
        }
    Assert.assertTrue(File(baseDir, deepDirName).isEmpty)
}