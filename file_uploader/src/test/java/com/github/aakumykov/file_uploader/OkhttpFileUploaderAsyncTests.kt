package com.github.aakumykov.file_uploader

import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.concurrent.CompletableFuture

class OkhttpFileUploaderAsyncTests : OkhttpFileUploaderTest() {

    lateinit var mFutureUploadingResult: CompletableFuture<OkhttpFileUploader.UploadingResult>

    @Before
    fun prepare() {
        mFutureUploadingResult = CompletableFuture<OkhttpFileUploader.UploadingResult>()
    }

    @Test
    fun when_async_postFile_to_good_url_then_SuccessResult() {

        okhttpFileUploader.postFile(uploadedFile,
            GOOD_POST_URL, object: OkhttpFileUploader.Callbacks {
            override fun onSuccess(successResult: OkhttpFileUploader.SuccessUploadingResult) {
                mFutureUploadingResult.complete(successResult)
            }

            override fun onUnSuccess(unSuccessResult: OkhttpFileUploader.UnsuccessUploadingResult) {
                mFutureUploadingResult.complete(unSuccessResult)
            }

            override fun onError(e: okio.IOException) {
            }
        })

        assertTrue(mFutureUploadingResult.get() is OkhttpFileUploader.SuccessUploadingResult)
    }


    @Test
    fun when_async_postFile_to_bad_url_then_UnsuccessResult() {

        okhttpFileUploader.postFile(uploadedFile,
            BAD_POST_URL, object: OkhttpFileUploader.Callbacks {
            override fun onSuccess(successResult: OkhttpFileUploader.SuccessUploadingResult) {
                mFutureUploadingResult.complete(successResult)
            }

            override fun onUnSuccess(unSuccessResult: OkhttpFileUploader.UnsuccessUploadingResult) {
                mFutureUploadingResult.complete(unSuccessResult)
            }

            override fun onError(e: okio.IOException) {
            }
        })

        assertTrue(mFutureUploadingResult.get() is OkhttpFileUploader.UnsuccessUploadingResult)
    }


    @Test
    fun when_async_postFile_to_unreachable_url_then_throws_io_exception() {

         val ioExceptionResult = CompletableFuture<IOException>()

        okhttpFileUploader.postFile(uploadedFile, UNREACHABLE_URL, object: OkhttpFileUploader.Callbacks {
            override fun onSuccess(successResult: OkhttpFileUploader.SuccessUploadingResult) {
            }

            override fun onUnSuccess(unSuccessResult: OkhttpFileUploader.UnsuccessUploadingResult) {
            }

            override fun onError(e: IOException) {
                ioExceptionResult.complete(e)
            }
        })

        assertTrue(ioExceptionResult.get() is IOException)
    }
}