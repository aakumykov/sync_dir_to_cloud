package com.github.aakumykov.file_uploader

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// TODO: внедрение зависимостей?
class OkhttpFileUploader @Inject constructor(private val okHttpClient: OkHttpClient) {

    private var currentCall: Call? = null

    /**
     * Асинхронно отправляет файл по назначению методом POST, для обратной связи использует коллбеки.
     * Может быть отменён методом cancel()
     */
    fun postFile(sourceFile: File, targetURL: String, callbacks: Callbacks): OkhttpFileUploader {

        val request = prepareRequest(sourceFile, targetURL)

        currentCall = okHttpClient.newCall(request)

        currentCall?.enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response) {

                callbacks.onUploadingComplete()

                if (response.isSuccessful)
                    callbacks.onSuccess(successUploadResult(response))
                else
                    callbacks.onUnSuccess(unsuccessUploadResult(response))
            }

            override fun onFailure(call: Call, e: IOException) {
                callbacks.onUploadingComplete()
                callbacks.onError(e)
            }
        })

        return this
    }


    /**
     * Отправляет файл по назначению методом POST, для обратной связи использует коллбеки.
     * Использует механизм Kotlin Coroutines.
     * Может быть отменён методом cancel()
     */
    suspend fun postFileSuspend(sourceFile: File, targetURL: String): UploadingResult {
        return suspendCoroutine { continuation ->
            val request = prepareRequest(sourceFile, targetURL)
            try {
                val response = okHttpClient.newCall(request).execute()
                continuation.resume(successUploadResult(response))
            } catch (e: IOException) {
                continuation.resumeWithException(e)
            }
        }
    }


    fun cancel() {
        currentCall?.cancel()
    }


    private fun prepareRequest(sourceFile: File, targetURL: String): Request {

        val requestBody: RequestBody = sourceFile.asRequestBody(DEFAULT_MEDIA_TYPE.toMediaType())

        return Request.Builder()
            .url(targetURL)
            .post(requestBody)
            .build()
    }


    private fun successUploadResult(response: Response): SuccessUploadingResult = SuccessUploadingResult(response.code, response.message)
    private fun unsuccessUploadResult(response: Response): UnsuccessUploadingResult = UnsuccessUploadingResult(response.code, response.message)


    sealed class UploadingResult (val isSuccess: Boolean, private val code: Int, private val message: String) {
        override fun toString(): String = UploadingResult::class.java.simpleName + " { $code: $message }"
        fun codeAndMessage(): String = "$code: $message"
    }
    class SuccessUploadingResult(code: Int, message: String) : UploadingResult(true, code, message)
    class UnsuccessUploadingResult(code: Int, message: String) : UploadingResult(false, code, message)


    interface Callbacks {

        /**
         * Вызывается при успешном, неуспешном или ошибочном завершении HTTP-запроса.
         */
        fun onUploadingComplete() {}

        /**
         * Вызывается при получении HTTP-кода ответа 2xx
         */
        fun onSuccess(successResult: SuccessUploadingResult)

        /**
         * Вызывается при получении HTTP-кода ответа, отличного от 2xx
         */
        fun onUnSuccess(unSuccessResult: UnsuccessUploadingResult)

        /**
         * Вызывается в случае исключения в ходе выполнения запроса.
         */
        fun onError(e: IOException)
    }


    companion object {
        private const val DEFAULT_MEDIA_TYPE: String = "application/octet-stream"
    }
}