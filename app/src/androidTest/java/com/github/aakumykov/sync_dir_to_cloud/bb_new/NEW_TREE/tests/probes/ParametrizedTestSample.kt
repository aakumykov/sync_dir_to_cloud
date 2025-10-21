package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.probes

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class ParametrizedTestSample(val numberOfRun: Int) : TBase() {

    companion object {
        private const val RUN_TEST_N_TIMES = 2

        @JvmStatic
        @Parameterized.Parameters
        fun data() : Collection<Int> {
            return List(RUN_TEST_N_TIMES) { it+1 }
        }
    }

    @Test
    fun shouldReturnExpectedRomanForArabic() {
        Assert.assertTrue(true);
    }
}