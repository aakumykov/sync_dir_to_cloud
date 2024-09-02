package com.github.aakumykov.sync_dir_to_cloud

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import org.junit.Test
import org.junit.runner.RunWith

class ProgressCalculatorUnitTest {

    @Test(expected = IllegalArgumentException::class)
    fun if_configured_throws_exception_on_negative_file_size() {
        ProgressCalculator(fileSize = -1, failOnNegativeFileSize = true)
    }

    @Test
    fun if_configured_not_throws_exception_on_negative_file_size() {
        ProgressCalculator(fileSize = -1, failOnNegativeFileSize = false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun if_configured_throws_exception_on_readed_non_zero_bytes_on_zero_size_file() {
        ProgressCalculator(fileSize = 0, failOnWrongReadedSize = true)
            .calcProgress(1)
    }

    @Test
    fun if_configured_not_throws_exception_on_readed_non_zero_bytes_on_zero_size_file() {
        ProgressCalculator(fileSize = 0, failOnWrongReadedSize = false)
            .calcProgress(1)
    }


}