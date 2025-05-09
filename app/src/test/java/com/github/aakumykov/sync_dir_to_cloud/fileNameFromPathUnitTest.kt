package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.functions.fileNameFromPath
import junit.framework.TestCase.*
import org.junit.Assert
import org.junit.Test
import java.io.File

class fileNameFromPathUnitTest {

    @Test
    fun when_empty_path_then_file_name_is_empty() {
        val fileName = fileNameFromPath("")
        Assert.assertEquals("", fileName)
    }

    @Test
    fun when_path_is_single_word_then_file_name_equals_it(){
        val path = "qwerty"
        assertEquals(
            path,
            fileNameFromPath(path)
        )
    }

    @Test
    fun when_root_path_then_name_is_empty(){
        val path = "/"
        val fileName = fileNameFromPath(path)
        assertEquals("", fileName)
    }

    @Test
    fun when_deep_path_ends_with_slash_then_file_name_equals_last_segment() {
        val fileName = "file1"
        assertEquals(
            fileName,
            fileNameFromPath("/path/to/$fileName/")
        )
    }

    @Test
    fun when_deep_path_not_ends_with_slash_then_file_name_equals_last_segment() {
        val fileName = "file1"
        assertEquals(
            fileName,
            fileNameFromPath("/path/to/$fileName")
        )
    }
}