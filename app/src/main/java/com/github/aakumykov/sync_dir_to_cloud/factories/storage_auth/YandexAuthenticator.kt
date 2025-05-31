package com.github.aakumykov.sync_dir_to_cloud.factories.storage_auth

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdkContract
import com.yandex.authsdk.internal.strategy.LoginType

class YandexAuthenticator (
    fragment: Fragment,
    private val storageAuthenticatorCallbacks: StorageAuthenticator.Callbacks
) : StorageAuthenticator {

    private val activityResultLauncher: ActivityResultLauncher<YandexAuthLoginOptions>

    init {
        val yandexAuthOptions = YandexAuthOptions(fragment.requireContext(), true)
        val yandexAuthSdkContract = YandexAuthSdkContract(yandexAuthOptions)

        activityResultLauncher = fragment.registerForActivityResult(yandexAuthSdkContract) { result: YandexAuthResult ->
            when (result) {
                is YandexAuthResult.Success -> storageAuthenticatorCallbacks.onCloudAuthSuccess(result.token.value)
                is YandexAuthResult.Failure -> storageAuthenticatorCallbacks.onCloudAuthFailed(result.exception)
                is YandexAuthResult.Cancelled -> storageAuthenticatorCallbacks.onCloudAuthFailed(Exception("Auth was cancelled"))
            }
        }
    }

    override fun startAuth() {
        // FIXME: вынести настройку типа аутентификации куда-нибудь вовне.
        activityResultLauncher.launch(YandexAuthLoginOptions(LoginType.NATIVE))
    }

    companion object {
        fun create(fragment: Fragment, callbacks: StorageAuthenticator.Callbacks): YandexAuthenticator {
            return YandexAuthenticator(fragment, callbacks)
        }
    }

}

