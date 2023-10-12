package com.github.aakumykov.sync_dir_to_cloud.cloud_auth

import android.app.Activity
import android.content.Intent
import com.github.aakumykov.yandex_auth_helper.YandexAuthHelper

class YandexDiskAuthenticator (
    activity: Activity,
    requestCode: Int,
    private val cloudAuthenticatorCallbacks: CloudAuthenticator.Callbacks?
) : CloudAuthenticator, YandexAuthHelper.Callbacks {

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
        cloudAuthenticatorCallbacks?.onCloudAuthSuccess(authToken)
    }

    override fun onYandexAuthFailed(errorMsg: String) {
        cloudAuthenticatorCallbacks?.onCloudAuthFailed(errorMsg)
    }
}

