package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.extensions.nullIfEmpty
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Test

class NullIfEmptyUnitTest {

    private val emptyList = emptyList<String>()
    private val fullIntList: List<Int> = listOf(1,2,3)
    private val fullStringList: List<String> = listOf("А","Б","В")
    private val fullObjectList: List<Any> = listOf(1,"qwerty")


    @Test
    fun return_null_when_list_is_empty() {
        val list = emptyList<String>()
        assertNull(list.nullIfEmpty())
    }

    @Test
    fun return_not_null_if_list_not_empty() {
        assertNotNull(fullObjectList.nullIfEmpty())
    }

    /*@Test
    fun size_of_returned_list_equals_original() {

    }*/
}