package com.github.aakumykov.sync_dir_to_cloud.cloud_auth

import android.app.Activity
import android.content.Intent
import com.github.aakumykov.yandex_auth_helper.YandexAuthHelper

class YandexAuthProcessor(
    activity: Activity,
    requestCode: Int,
) : CloudAuthProcessor, YandexAuthHelper.Callbacks {

    private val yandexAuthHelper: YandexAuthHelper

    init {
        yandexAuthHelper = YandexAuthHelper(activity, requestCode, this)
    }


    override fun startCloudAuth() {
        yandexAuthHelper.beginAuthorization()
    }

    override fun processCloudAuthResult(requestCode: Int, resultCode: Int, data: Intent?) {
        yandexAuthHelper.processAuthResult(requestCode, resultCode, data)
    }


    override fun onYandexAuthSuccess(authToken: String) {

    }

    override fun onYandexAuthFailed(errorMsg: String) {

    }
}

