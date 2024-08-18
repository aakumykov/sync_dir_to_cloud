package com.github.aakumykov.sync_dir_to_cloud.counting_streams

import android.util.Log
import java.io.File
import java.io.FileInputStream

class CountingFileInputStream(
    file: File,
    private val callbackTriggeringIntervalBytes: Long = 8192,
    private val onReadedBytesCountChanged: (Int) -> Unit,
)
    : FileInputStream(file)
{
    private var totalReadBytesCount: Int = 0
    private var callbackTriggeringBytesCounter: Long = 0

    override fun read(b: ByteArray?, off: Int, len: Int): Int {
        return super.read(b, off, len).let { readCount ->

            Log.d(TAG, "$TAG.read(), readCount = $readCount")

            totalReadBytesCount += readCount
            callbackTriggeringBytesCounter += readCount
//            Log.d(TAG, "callbackTriggeringBytesCounter=$callbackTriggeringBytesCounter")

            val isCallbackThresholdOverloaded = callbackTriggeringBytesCounter >= callbackTriggeringIntervalBytes

            if (isCallbackThresholdOverloaded || readCount < 0) {
                onReadedBytesCountChanged.invoke(totalReadBytesCount)
                callbackTriggeringBytesCounter = 0
            }

            readCount
        }
    }

    companion object {
        val TAG: String = CountingFileInputStream::class.java.simpleName
    }
}