package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.extensions.round
import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.random.Random

class FloatRoundExtensionUnitTest {

    @Test
    fun when_round_zero_zero_then_result_is_zero() {
        val roundedNum = ZERO_FLOAT_NUM.round(1)
        assertEquals(ZERO_DOT_ZERO_STRING, roundedNum.toString())
    }

    @Test
    fun when_round_posotive_numbers_then_length_matches_decimal_digits_count() {
        for (n in 1..5) {
            assertEquals(
                n+2,
                RANDOM_FLOAT_NUM.round(n).toString().length
            )
        }
    }

    @Test
    fun when_round_negative_numbers_then_length_matches_decimal_digits_count() {
        for (n in 1..5) {
            assertEquals(
                n+3,
                NEGATIVE_RANDOM_FLOAT_NUM.round(n).toString().length
            )
        }
    }

    @Test
    fun when_round_positive_number_to_zero_decimal_digits_then_result_is_zero_dot_zero() {
        assertEquals(ZERO_DOT_ZERO_STRING, MANUAL_FLOAT_NUM.round(0).toString())
    }


    @Test
    fun when_round_negative_number_to_zero_decimal_digits_then_result_is_zero_dot_zero() {
        assertEquals(ZERO_DOT_ZERO_STRING, (-1* MANUAL_FLOAT_NUM).round(0).toString())
    }


    companion object {
        val RANDOM_FLOAT_NUM = Random.nextFloat()
        val NEGATIVE_RANDOM_FLOAT_NUM get() = -1f * RANDOM_FLOAT_NUM
        const val ZERO_FLOAT_NUM = 0.0f
        const val MANUAL_FLOAT_NUM = 0.12345f
        const val ZERO_DOT_ZERO_STRING = "0.0"
    }
}