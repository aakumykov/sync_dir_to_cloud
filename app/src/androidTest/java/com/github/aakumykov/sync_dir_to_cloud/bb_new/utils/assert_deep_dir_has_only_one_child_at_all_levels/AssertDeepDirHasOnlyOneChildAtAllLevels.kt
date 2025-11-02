package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.assert_deep_dir_has_only_one_child_at_all_levels

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_children_count.childrenCount
import org.junit.Assert
import java.io.File

fun assertDeepDirHasOnlyOneChildAtAllLevels(baseDir: File, deepDirName: String) {
    var partialDeepDirName = ""
    for (dirName in deepDirName.split(CloudWriter.DS)) {
        partialDeepDirName += CloudWriter.DS + dirName
        Assert.assertEquals(1, File(baseDir, partialDeepDirName).childrenCount)
    }
}