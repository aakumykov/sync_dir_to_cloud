package com.github.aakumykov.sync_dir_to_cloud

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

class ProgressCalculatorInstrumentedTest {

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
        for (readedBytes in 0..1000) {
            val progress = progressCalculator.calcProgress(readedBytes.toLong())
            assertEquals(readedBytes/100f, progress)
            val progressAsPartOf100 = progressCalculator.progressAsPartOf100(progress)
            assertEquals(readedBytes, progressAsPartOf100)
        }
    }
}