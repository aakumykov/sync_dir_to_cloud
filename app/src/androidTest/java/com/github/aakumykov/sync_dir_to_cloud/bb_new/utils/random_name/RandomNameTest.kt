package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name

import org.junit.Assert
import org.junit.Test

class RandomNameTest {

    companion object {
        private const val UNIQUENESS_CHECK_COUNT = 1_000_000
    }

    @Test
    fun returns_not_empty_string() {
        Assert.assertFalse(randomName.isEmpty())
    }

    // Этим тестом было обнаружено присутствие не-уникальных имён,
    // что показывает важность тестирования 👍
    @Test
    fun returns_diff_names_many_times() {
        buildList {
            repeat(UNIQUENESS_CHECK_COUNT) {
                add(randomName)
            }
        }.also {
            Assert.assertEquals(
                UNIQUENESS_CHECK_COUNT,
                it.toSet().size
            )
        }
    }
}