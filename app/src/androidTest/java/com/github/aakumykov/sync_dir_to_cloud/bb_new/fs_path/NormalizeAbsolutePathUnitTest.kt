package com.github.aakumykov.sync_dir_to_cloud.bb_new.fs_path

import com.github.aakumykov.sync_dir_to_cloud.functions.normalizeAbsolutePath
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Assert
import org.junit.Test

class normalizeAbsolutePathInstrumentedTests : TestCase() {

    @Test
    fun when_normalize_path_with_tailing_slash_then_result_is_without_slash() {
        Assert.assertEquals(
            FilePathSamples.DEEP_PATH_TO_DIR_WITHOUT_SLASH,
            normalizeAbsolutePath(FilePathSamples.DEEP_PATH_TO_DIR_WITH_SLASH)
        )
    }

    @Test
    fun when_normalize_path_with_multi_slashes_then_is_becomes_without_slashes() {
        Assert.assertEquals(
            FilePathSamples.DEEP_PATH_TO_FILE,
            normalizeAbsolutePath(FilePathSamples.MULTI_SLASH_DEEP_PATH_TO_FILE)
        )
    }

    @Test
    fun when_normalize_multi_slash_path_with_tail_slash_then_it_becomes_without_that_slashes(){
        Assert.assertEquals(
            FilePathSamples.DEEP_PATH_TO_DIR_WITHOUT_SLASH,
            normalizeAbsolutePath(FilePathSamples.MULTI_SLASH_DEEP_PATH_TO_DIR_WITH_TAIL_SLASH)
        )
    }


    @Test
    fun when_normalize_root_path_then_it_remains_root(){
        Assert.assertEquals(
            FilePathSamples.ROOT_PATH,
            normalizeAbsolutePath(FilePathSamples.ROOT_PATH)
        )
    }


    @Test
    fun when_normalize_empty_path_then_it_remains_empty_path(){
        Assert.assertEquals(
            FilePathSamples.EMPTY_PATH,
            normalizeAbsolutePath(FilePathSamples.EMPTY_PATH)
        )
    }

    @Test
    fun when_normalize_witespace_path_then_it_remains_witespace_path(){
        Assert.assertEquals(
            FilePathSamples.WHITE_SPACE_PATH,
            normalizeAbsolutePath(FilePathSamples.WHITE_SPACE_PATH)
        )
    }

    @Test
    fun when_normalize_tabulation_path_then_it_remains_tabulation_path(){
        Assert.assertEquals(
            FilePathSamples.TAB_PATH,
            normalizeAbsolutePath(FilePathSamples.TAB_PATH)
        )
    }


    @Test
    fun when_normalize_tab_and_spaces_path_then_it_remains_original(){
        Assert.assertEquals(
            FilePathSamples.TABS_AND_SPACES_PATH,
            normalizeAbsolutePath(FilePathSamples.TABS_AND_SPACES_PATH)
        )
    }


    @Test
    fun when_normalize_path_with_spaces_then_it_preserve_them(){
        Assert.assertEquals(
            FilePathSamples.PATH_WITH_SPACES,
            normalizeAbsolutePath(FilePathSamples.PATH_WITH_SPACES)
        )
    }


    @Test
    fun when_normalize_simple_single_word_relative_path_then_it_is_unchanged(){
        Assert.assertEquals(
            FilePathSamples.SIMPLE_FILE_NAME,
            normalizeAbsolutePath(FilePathSamples.SIMPLE_FILE_NAME)
        )
    }


    @Test
    fun when_normalize_complex_single_word_relative_path_then_it_is_unchanged(){
        Assert.assertEquals(
            FilePathSamples.COMPLEX_FILE_NAME,
            normalizeAbsolutePath(FilePathSamples.COMPLEX_FILE_NAME)
        )
    }


    @Test
    fun when_relative_deep_path_then_is_remains_original(){
        Assert.assertEquals(
            FilePathSamples.RELATIVE_DEEP_PATH,
            normalizeAbsolutePath(FilePathSamples.RELATIVE_DEEP_PATH)
        )
    }

    @Test
    fun then_relative_deep_path_with_multi_slashes_then_it_becomes_with_single_slashes(){
        Assert.assertEquals(
            FilePathSamples.RELATIVE_DEEP_PATH,
            normalizeAbsolutePath(FilePathSamples.RELATIVE_PATH_WITH_MULTI_SLASHES),
        )
    }

    @Test
    fun then_relative_deep_path_with_tail_slash_then_it_becomes_without_it(){
        Assert.assertEquals(
            FilePathSamples.RELATIVE_DEEP_PATH,
            normalizeAbsolutePath(FilePathSamples.RELATIVE_DEEP_PATH_TAIL_SLASH),
        )
    }

    @Test
    fun then_relative_deep_path_with_multi_slahes_and_tail_slash_then_it_becomes_without_it(){
        Assert.assertEquals(
            FilePathSamples.RELATIVE_DEEP_PATH,
            normalizeAbsolutePath(FilePathSamples.RELATIVE_PATH_WITH_MULTI_SLASHES_AND_TAIL_SLASH),
        )
    }
}