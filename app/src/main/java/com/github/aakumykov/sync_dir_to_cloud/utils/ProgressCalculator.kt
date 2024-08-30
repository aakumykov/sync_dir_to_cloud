package com.github.aakumykov.sync_dir_to_cloud.utils

import com.github.aakumykov.sync_dir_to_cloud.extensions.round

/**
 * Копирует данные SyncObject-а из источника в приёмник указанный в SyncTask.
 */
// TODO: внедрять объект-считатель прогресса

class ProgressCalculator(private val fullFileSize: Long) {

    fun calcProgress(readedBytes: Long): Float {
        // Если размер файла 0, приходит "считано 0" и прогресс сразу становится 100%.
        return if (0L == readedBytes && 0L == fullFileSize) 1.0f
        else (1f*readedBytes / fullFileSize).round(2)
    }

    fun progressAsPartOf100(progress: Float): Int = Math.round(progress * 100)
}