package com.github.aakumykov.sync_dir_to_cloud.utils

import java.io.BufferedInputStream
import java.io.InputStream

class CountingBufferedInputStream(
    private val inputStream: InputStream,
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


    override fun close() {
        super.close()
        inputStream.close()
    }


    private fun summarizeAndCallBack(count: Int) {

        if (-1 == count) {
            invokeCallback()
            return
        }

        readedBytesCount += count
        callbackTriggeringBytesCounter += count

        val isCallbackThresholdExceed = callbackTriggeringBytesCounter >= callbackTriggeringIntervalBytes

        if (isCallbackThresholdExceed || count < 0) {
            callbackTriggeringBytesCounter = 0
            invokeCallback()
        }
    }


    private fun invokeCallback() {
        readingCallback.onReadCountChanged(readedBytesCount)
    }


    fun interface ReadingCallback {
        fun onReadCountChanged(count: Long)
    }


    companion object {
        const val DEFAULT_BUFFER_SIZE = 8192
    }
}