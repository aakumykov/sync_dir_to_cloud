package com.github.aakumykov.sync_dir_to_cloud.utils.counting_buffered_streams

import com.github.aakumykov.kotlin_playground.counting_buffered_streams.CoroutineScopedBufferedInputStream
import kotlinx.coroutines.CoroutineScope
import java.io.InputStream
import java.util.concurrent.TimeUnit

class DelayedInputStream(
    private val delayMs: Long,
    inputStream: InputStream,
    coroutineScope: CoroutineScope,
    readingCallback: ReadingCallback,
)
    : CoroutineScopedBufferedInputStream(
        inputStream = inputStream,
        coroutineScope = coroutineScope,
        readingCallback = readingCallback,
    )
{
    override fun read(b: ByteArray?, off: Int, len: Int): Int {
        return super.read(b, off, len).apply {
            TimeUnit.MILLISECONDS.sleep(delayMs)
        }
    }
}