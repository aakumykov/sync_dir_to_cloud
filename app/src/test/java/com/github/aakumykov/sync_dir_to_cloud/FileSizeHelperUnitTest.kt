package com.github.aakumykov.sync_dir_to_cloud

import android.util.Log
import androidx.core.util.Supplier
import com.github.aakumykov.sync_dir_to_cloud.utils.FileSizeHelper
import junit.framework.TestCase.assertTrue
import org.junit.Test
import kotlin.random.Random

class FileSizeHelperUnitTest {

    private val randomInt: Int get() = Random.nextInt(1,500)
    private val randomBytes: Long get() = randomInt * 1L
    private val randomKilobytes: Long get() = randomBytes * 1024L
    private val randomMegabytes: Long get() = randomKilobytes * 1024L
    private val randomGigabytes: Long get() = randomMegabytes * 1024L
    private val randomTerabytes: Long get() = randomGigabytes * 1024L
    private val randomPetabytes: Long get() = randomTerabytes * 1024L

    @Test
    fun test_all_variants() {
        test(BYTES_SUFFIX) { randomBytes }
        test(KILOBYTES_SUFFIX) { randomKilobytes }
        test(MEGABYTES_SUFFIX) { randomMegabytes }
        test(GIGABYTES_SUFFIX) { randomGigabytes }
        test(TERABYTES_SUFFIX) { randomTerabytes }
        test(PETABYTES_SUFFIX) { randomPetabytes }
    }

    private fun test(suffix: String, bytesCountSupplier: Supplier<Long>) {
        repeat(Random.nextInt(10,100)) {
            FileSizeHelper.fileSize(bytesCountSupplier.get()).also {
                assertTrue(it.endsWith(suffix))
                assertTrue(it.matches("^[0-9]+\\s+${suffix}$".toRegex()))
            }
        }
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }

    companion object {
        val TAG: String = FileSizeHelperUnitTest::class.java.simpleName
        const val BYTES_SUFFIX = "B"
        const val KILOBYTES_SUFFIX = "kB"
        const val MEGABYTES_SUFFIX = "MB"
        const val GIGABYTES_SUFFIX = "GB"
        const val TERABYTES_SUFFIX = "TB"
        const val PETABYTES_SUFFIX = "PB"
    }
}