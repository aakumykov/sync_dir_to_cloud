package com.github.aakumykov.sync_dir_to_cloud

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

class ProgressCalculatorUnitTest {


    @Test
    fun when_works_with_zero_size_file_then_progress_is_1f_immediately(){
        val progressCalculator = ProgressCalculator(0)
        assertEquals(1f, progressCalculator.calcProgress(0))
    }


    @Test
    fun when_works_with_zero_size_file_then_progress_is_100_percent_immediately(){
        val progressCalculator = ProgressCalculator(0)
        val progress = progressCalculator.calcProgress(0)
        assertEquals(100, progressCalculator.progressAsPartOf100(progress))
    }


    @Test
    fun when_work_with_non_zero_size_file_then_progress_grows_due_to_readed_bytes_count() {

        val fileSize = 100L
        val progressCalculator = ProgressCalculator(fileSize)

        for (readedBytes in 0..fileSize.toInt()) {

            val progress = progressCalculator.calcProgress(readedBytes.toLong())
            assertEquals(readedBytes/100f, progress)

            val progressAsPartOf100 = progressCalculator.progressAsPartOf100(progress)
            assertEquals(readedBytes, progressAsPartOf100)

            println("прочитано: $readedBytes из $fileSize, прогресс: $progress | ${progressAsPartOf100}%")
        }
    }



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