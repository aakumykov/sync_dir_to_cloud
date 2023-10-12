package com.github.aakumykov.sync_dir_to_cloud.cloud_auth

import android.app.Activity
import android.os.Bundle
import com.github.aakumykov.yandex_auth_helper.YandexAuthHelper

class YandexAuthProcessor(
    activity: Activity,
    requestCode: Int,
) : CloudAuthProcessor, YandexAuthHelper.Callbacks {

    val yandexAuthHelper: YandexAuthHelper

    init {
        yandexAuthHelper = YandexAuthHelper(activity, requestCode, this)
    }


    override fun startCloudAuth() {
        TODO("Not yet implemented")
    }

    override fun processCloudAuthResult(data: Bundle?) {
        TODO("Not yet implemented")
    }


    override fun onYandexAuthSuccess(authToken: String) {
        TODO("Not yet implemented")
    }

    override fun onYandexAuthFailed(errorMsg: String) {
        TODO("Not yet implemented")
    }
}