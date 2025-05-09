package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.functions.relativeParentDirPath
import junit.framework.TestCase.assertEquals
import org.junit.Test

class relativeParentDirPathUnitTest {

    companion object {
        private const val BASE_PATH="/base/path"

        const val EMPTY_PATH=""

        const val DEEP_PATH_TO_FILE="$BASE_PATH/path/to/file1"
        const val DEEP_PATH_TO_DIR_WITHOUT_SLASH="$BASE_PATH/path/to/dir"
        const val DEEP_PATH_TO_DIR_WITH_SLASH="$BASE_PATH/path/to/dir/"

        const val MULTI_SLASH_PATH_TO_FILE="$BASE_PATH/path//to///file"
        const val MULTI_SLASH_PATH_TO_DIR_WITH_SLASH="$BASE_PATH//path/to//dir///"
        const val MULTI_SLASH_PATH_TO_DIR_WITHOUT_SLASH="$BASE_PATH//path/to//dir"

        const val PATH_TO_FILE_OUTSIDE_BASE_PATH="/path/to/other/file"
        const val PATH_TO_DIR_OUTSIDE_BASE_PATH_WITHOUT_SLASH="/path/to/other/dir"
        const val PATH_TO_DIR_OUTSIDE_BASE_PATH_WITH_SLASH="/path/to/other/dir/"
    }


    // FIXME: какое поведение здесь должно быть?
    @Test
    fun when_empty_path_then_result_is_empty(){
        assertEquals("", relativeParentDirPath(EMPTY_PATH, BASE_PATH))
    }

}