package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_bytes

import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class RandomBytesTest {

    /**
     * Вызов по умолчанию возвращает непустой массив байт
     * [default_property_returns_not_empty_byte_array]
     * [default_function_returns_not_empty_byte_array]
     *
     * Вызов с аргументом "ноль" возвращаем пустой массив байт
     * [argument_zero_returns_empty_byte_array]
     *
     * Вызов с разными ненулевыми аргументами возвращает массив такой же длины
     * [input_number_equals_result_length]
     */

    @Test
    fun default_property_returns_not_empty_byte_array() {
        Assert.assertFalse(randomBytes.isEmpty())
    }

    @Test
    fun default_function_returns_not_empty_byte_array() {
        Assert.assertFalse(randomBytes().isEmpty())
    }

    @Test
    fun argument_zero_returns_empty_byte_array() {
        Assert.assertEquals(0, randomBytes(0).size)
    }

    @Test
    fun input_number_equals_result_length() {
        for (i in 1..Random.nextInt(1024)) {
            Assert.assertEquals(i, randomBytes(i).size)
        }
    }
}