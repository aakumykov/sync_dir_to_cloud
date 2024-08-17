package com.github.aakumykov.sync_dir_to_cloud.counting_streams

import java.io.OutputStream

class CountingOutputStream(
    private val outputStream: OutputStream,
    private val callback: WritingCallback,
) : OutputStream() {

    private var writtenBytesCount: Long = 0

    override fun write(b: Int) {
        outputStream.write(b).also {
            summarizeAndCallBack(1)
        }
    }

    override fun write(b: ByteArray?) {
        outputStream.write(b).also {
            summarizeAndCallBack(b?.size ?: 0)
        }
    }

    override fun write(b: ByteArray?, off: Int, len: Int) {
        outputStream.write(b,off,len).also {
            summarizeAndCallBack(b?.size ?: 0)
        }
    }


    private fun summarizeAndCallBack(count: Int) {
        writtenBytesCount += count
        callback.onWriteCountChanged(writtenBytesCount)
    }


    fun interface WritingCallback {
        fun onWriteCountChanged(count: Long)
    }
}