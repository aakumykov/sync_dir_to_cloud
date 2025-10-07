package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.api_number

import org.junit.Assert
import org.junit.Test

class ApiNumberTest {

    @Test
    fun api_number_returns_number() {
        Assert.assertEquals(
            apiNumber.toLong().toInt(),
            apiNumber,
        )
    }
}