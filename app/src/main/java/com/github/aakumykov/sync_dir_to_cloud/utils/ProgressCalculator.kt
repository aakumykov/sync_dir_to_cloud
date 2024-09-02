package com.github.aakumykov.sync_dir_to_cloud.utils

import com.github.aakumykov.sync_dir_to_cloud.extensions.round

/**
 * @param fileSize Размер файла, участвующего в расчётах.
 * @param failOnWrongFileSize Бросать исключение, если прочитано больше 0 (нуля) данных
 * на файле нулевого размера.
 */
class ProgressCalculator(private val fileSize: Long, private val failOnWrongFileSize: Boolean = false) {

    fun calcProgress(readedBytes: Long): Float {
        return if (0L == fileSize) {
            if (!failOnWrongFileSize) 1.0f
            else throw IllegalArgumentException("Read more than zero bytes ($readedBytes) on zero-size file.")
        }
        else (1f*readedBytes / fileSize).round(2)
    }

    fun progressAsPartOf100(progress: Float): Int = Math.round(progress * 100)
}