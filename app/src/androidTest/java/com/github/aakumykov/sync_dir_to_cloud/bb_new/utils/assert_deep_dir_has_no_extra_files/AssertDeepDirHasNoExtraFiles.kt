package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.assert_deep_dir_has_no_extra_files

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_children_count.childrenCount
import org.junit.Assert
import java.io.File

fun assertDeepDirTreeHasNoExtraFiles(baseDir: File, deepDirName: String) {
    val list: MutableList<String> = mutableListOf(* deepDirName.split(CloudWriter.DS).toTypedArray())

    list.removeLast()

    list.reduce { a,b ->
            val dirIntoDeepName = a + CloudWriter.DS + b
            val dirIntoDeep = File(baseDir, dirIntoDeepName)
            Assert.assertEquals(1, dirIntoDeep.childrenCount)
            dirIntoDeepName
        }
}