package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_writer

import java.io.IOException
import java.io.InputStream
import kotlin.jvm.Throws

class CountingInputStream(
    private val inputStream: InputStream,
    private val readingCallback: ReadingCallback
) : InputStream() {

    private var readBytesCount: Long = 0

    @Throws(IOException::class)
    override fun read(): Int {
        return inputStream.read()
    }

    override fun read(b: ByteArray?): Int {
        return super.read(b).let { justReadCount ->
            if (justReadCount > 0)
                summarizeAndCallBack(justReadCount)
            justReadCount
        }
    }


    private fun summarizeAndCallBack(count: Int) {
        readBytesCount += count
        readingCallback.onReadCountChanged(readBytesCount)
    }


    fun interface ReadingCallback {
        fun onReadCountChanged(count: Long)
    }
}