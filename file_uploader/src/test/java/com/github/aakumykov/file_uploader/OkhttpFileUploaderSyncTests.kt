package com.github.aakumykov.file_uploader

import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.io.IOException


class OkhttpFileUploaderSyncTests : OkhttpFileUploaderTest() {

    /*@Test
    fun when_sync_postFile_to_good_url_then_SuccessResult() {
        val result = okhttpFileUploader.postFile(uploadedFile, GOOD_POST_URL)
        assertTrue(result is OkhttpFileUploader.SuccessUploadingResult)
    }

    @Test
    fun when_sync_postFile_to_bad_url_then_ErrorResult() {
        val result = okhttpFileUploader.postFile(uploadedFile, BAD_POST_URL)
        assertTrue(result is OkhttpFileUploader.UnsuccessUploadingResult)
    }

    @Test(expected = IOException::class)
    fun when_sync_post_to_unreachable_url_then_throws_IOException() {
        okhttpFileUploader.postFile(uploadedFile, UNREACHABLE_URL)
    }*/
}
