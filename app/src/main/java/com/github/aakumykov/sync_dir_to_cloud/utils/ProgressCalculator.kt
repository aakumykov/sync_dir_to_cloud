package com.github.aakumykov.sync_dir_to_cloud.utils

import com.github.aakumykov.sync_dir_to_cloud.extensions.round

/**
 * @param fileSize Размер файла, участвующего в расчётах.
 * @param failOnNegativeFileSize Бросать исключение, если размер файла < 0.
 * @param failOnWrongReadedSize Бросать исключение, если прочитано больше 0 (нуля) данных
 * на файле нулевого размера.
 */
class ProgressCalculator(
    private val fileSize: Long,
    private val failOnNegativeFileSize: Boolean = true,
    private val failOnWrongReadedSize: Boolean = false
) {
    init {
        if (fileSize < 0 && failOnNegativeFileSize)
            throw IllegalArgumentException("$TAG is configured to fail on negative file size. " +
                    "If you want to change this behaviour pass 'failOnNegativeFileSize=false' " +
                    "to it's constructor")
    }

    fun calcProgress(readedBytes: Long): Float {
        return if (0L == fileSize) {
            if (!failOnWrongReadedSize) 1.0f
            else throw IllegalArgumentException("Read more than zero bytes ($readedBytes) on zero-size file.")
        }
        else (1f*readedBytes / fileSize).round(2)
    }

    fun progressAsPartOf100(progress: Float): Int = Math.round(progress * 100)

    companion object {
        val TAG: String = ProgressCalculator::class.java.simpleName
    }
}