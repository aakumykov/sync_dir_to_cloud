package com.github.aakumykov.sync_dir_to_cloud.cloud_auth

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdkContract
import com.yandex.authsdk.internal.strategy.LoginType

class YandexDiskAuthenticator (
    fragment: Fragment,
    private val loginType: LoginType,
    private val cloudAuthenticatorCallbacks: CloudAuthenticator.Callbacks
) : CloudAuthenticator {

    private val activityResultLauncher: ActivityResultLauncher<YandexAuthLoginOptions>

    init {
        val yandexAuthOptions = YandexAuthOptions(fragment.requireContext(), true)
        val yandexAuthSdkContract = YandexAuthSdkContract(yandexAuthOptions)

        activityResultLauncher = fragment.registerForActivityResult(yandexAuthSdkContract) { result ->
            val yandexAuthToken = result?.getOrElse { throwable ->
                cloudAuthenticatorCallbacks.onCloudAuthFailed(throwable)
            }
            cloudAuthenticatorCallbacks.onCloudAuthSuccess(yandexAuthToken as String)
        }
    }

    override fun startAuth() {
        activityResultLauncher.launch(YandexAuthLoginOptions(loginType))
    }

}

