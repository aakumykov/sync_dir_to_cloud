package com.github.aakumykov.sync_dir_to_cloud.counting_streams

import java.io.File
import java.io.FileInputStream

class CountingFileInputStream(
    file: File,
    private val onReadedBytesCountChanged: (Int) -> Unit
)
    : FileInputStream(file)
{
    private var readCount: Int = 0
    private var totalReadBytesCount: Int = 0

    override fun read(): Int {
        return super.read().let {
            readCount = it
            publishReadedCount()
            readCount
        }
    }

    override fun read(b: ByteArray?, off: Int, len: Int): Int {
        return super.read(b, off, len).let {
            readCount = it
            publishReadedCount()
            readCount
        }
    }

    override fun read(b: ByteArray?): Int {
        return super.read(b).let {
            readCount = it
            publishReadedCount()
            readCount
        }
    }

    private fun publishReadedCount() {
        if (readCount >= 0) {
            totalReadBytesCount += readCount
            onReadedBytesCountChanged.invoke(totalReadBytesCount)
        }
    }
}