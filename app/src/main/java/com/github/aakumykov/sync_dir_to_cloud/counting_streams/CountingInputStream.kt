package com.github.aakumykov.sync_dir_to_cloud.counting_streams

import java.io.IOException
import java.io.InputStream
import kotlin.jvm.Throws

class CountingInputStream(
    private val inputStream: InputStream,
    private val callbackTriggeringIntervalBytes: Long = 8192,
    private val readingCallback: ReadingCallback,
) : InputStream() {

    private var readedBytesCount: Long = 0
    private var callbackTriggeringBytesCounter: Long = 0

    @Throws(IOException::class)
    override fun read(): Int {
        return inputStream.read().let { justReadByte ->
            summarizeAndCallBack(1)
            justReadByte
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
}