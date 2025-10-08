package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name

import com.github.aakumykov.cloud_writer.CloudWriter
import org.junit.Assert
import org.junit.Test

class RandomDeepDirNameTest {

    companion object {
        private const val UNIQUENESS_CHECKING_COUNT = 1_000
    }

    @Test
    fun returns_not_empty_name() {
        Assert.assertFalse(randomDeepDirName.isEmpty())
    }

    @Test
    fun depth_bounds_are_effective() {
        repeat(10) { i ->
            val n = i+1
            val parts = randomDeepDirName(n,n).split(CloudWriter.DS)
            Assert.assertEquals(n, parts.size)
        }
    }

    @Test
    fun returns_different_names_many_times() {
        buildList {
            repeat(UNIQUENESS_CHECKING_COUNT) {
                add(randomDeepDirName)
            }
        }.also {
            Assert.assertEquals(UNIQUENESS_CHECKING_COUNT, it.toSet().size)
        }
    }
}