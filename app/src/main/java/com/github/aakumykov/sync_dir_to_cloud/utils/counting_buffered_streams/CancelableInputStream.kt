package com.github.aakumykov.sync_dir_to_cloud.utils.counting_buffered_streams

import com.github.aakumykov.kotlin_playground.counting_buffered_streams.CountingBufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

class CancelableInputStream(
    inputStream: InputStream,
    bufferSize: Int = 8192,
    private val delayMs: Long = 0L,
    private val cancellationMarker: CancellationMarker,
    readingCallback: ReadingCallback,
)
    : CountingBufferedInputStream(inputStream, bufferSize, readingCallback)
{
    override fun read(b: ByteArray?, off: Int, len: Int): Int {

        if (cancellationMarker.isCancelled)
            throw StreamCancellationException()

        TimeUnit.MILLISECONDS.sleep(delayMs)

        return super.read(b, off, len)
    }

    abstract class CancellationMarker {
        var isCancelled: Boolean = false
    }

    class StreamCancellationException : IOException("Stream was cancelled by user")
}