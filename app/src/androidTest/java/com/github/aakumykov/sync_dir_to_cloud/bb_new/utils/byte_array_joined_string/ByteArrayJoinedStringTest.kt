package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.byte_array_joined_string

import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes.randomBytes
import org.junit.Assert
import org.junit.Test

class ByteArrayJoinedStringTest {

    private val manualData = byteArrayOf(1,2,3,4,5)

    @Test
    fun join_manual_bytes() {
        Assert.assertEquals(
            manualData.joinToString(),
            manualData.joinedString
        )
    }

    @Test
    fun join_random_bytes() {
        repeat(1000) { n ->
            val randomData = randomBytes(n)
            Assert.assertEquals(
                randomData.joinToString(),
                randomData.joinedString
            )
        }
    }
}