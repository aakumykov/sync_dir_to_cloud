package com.github.aakumykov.sync_dir_to_cloud.utils

import java.io.BufferedInputStream
import java.io.InputStream

class CountingBufferedInputStream(
    inputStream: InputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    private val callbackTriggeringIntervalBytes: Int = DEFAULT_BUFFER_SIZE,
    private val readingCallback: ReadingCallback,
)
    : BufferedInputStream(inputStream, bufferSize)
{
    private var readedBytesCount: Long = 0
    private var callbackTriggeringBytesCounter: Long = 0


    override fun read(b: ByteArray?, off: Int, len: Int): Int {
        return super.read(b, off, len).let { count ->
            summarizeAndCallBack(count)
            count
        }
    }


    private fun summarizeAndCallBack(count: Int) {

        readedBytesCount += count
        callbackTriggeringBytesCounter += count

        val isCallbackThresholdExceed = callbackTriggeringBytesCounter >= callbackTriggeringIntervalBytes

        if (isCallbackThresholdExceed || count < 0) {
            readingCallback.onReadCountChanged(readedBytesCount)
            callbackTriggeringBytesCounter = 0
        }
    }


    fun interface ReadingCallback {
        fun onReadCountChanged(count: Long)
    }


    companion object {
        const val DEFAULT_BUFFER_SIZE = 8192
    }
}