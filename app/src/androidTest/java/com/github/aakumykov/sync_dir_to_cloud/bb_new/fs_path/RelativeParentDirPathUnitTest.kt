package com.github.aakumykov.sync_dir_to_cloud.bb_new.fs_path

import com.github.aakumykov.file_lister_navigator_selector.dir_creator_dialog.DirCreatorDialog.Companion.BASE_PATH
import com.github.aakumykov.sync_dir_to_cloud.bb_new.fs_path.FilePathSamples.EMPTY_PATH
import com.github.aakumykov.sync_dir_to_cloud.functions.relativeParentDirPath
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import junit.framework.TestCase.assertEquals
import org.junit.Test

class relativeParentDirPathInstrumentedTests : TestCase() {

    @Test
    fun when_empty_path_then_result_is_empty(){
        assertEquals("", relativeParentDirPath(EMPTY_PATH, BASE_PATH))
    }

}