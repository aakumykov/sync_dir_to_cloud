package com.github.aakumykov.sync_dir_to_cloud.iterable_ext

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.*

class IterableCommonFunctions {

    /*@Test
    fun when_compare_iterable_with_equals_sizes_then_ok() {
        try { assertSizeEquals(listOf(1), listOf(2)) }
        catch (e: Throwable) {  }
    }*/

    companion object {
        fun <T> assertSizeEquals(first: Iterable<T>, second: Iterable<T>) {
            assertEquals(first.toList().size, second.toList().size)
        }

        fun <T> assertContainsAllElements(inspected: Iterable<T>, elements: Iterable<T>) {
            assertTrue(
                inspected.toMutableList().apply {
                    removeAll(elements.toSet())
                }.isEmpty()
            )
        }
    }
}