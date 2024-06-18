package com.github.aakumykov.sync_dir_to_cloud.factories.cloud_auth

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit.AuthEditFragment
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdkContract
import com.yandex.authsdk.internal.strategy.LoginType

class YandexAuthenticator (
    fragment: Fragment,
    private val cloudAuthenticatorCallbacks: CloudAuthenticator.Callbacks
) : CloudAuthenticator {

    private val activityResultLauncher: ActivityResultLauncher<YandexAuthLoginOptions>

    init {
        val yandexAuthOptions = YandexAuthOptions(fragment.requireContext(), true)
        val yandexAuthSdkContract = YandexAuthSdkContract(yandexAuthOptions)

        activityResultLauncher = fragment.registerForActivityResult(yandexAuthSdkContract) { result ->
            result.getOrNull()?.also {
                cloudAuthenticatorCallbacks.onCloudAuthSuccess(it.value)
            } ?: cloudAuthenticatorCallbacks.onCloudAuthFailed(Throwable("Auth was cancelled or result is null."))
        }
    }

    override fun startAuth() {
        // FIXME: вынести настройку типа аутентификации куда-нибудь вовне.
        activityResultLauncher.launch(YandexAuthLoginOptions(LoginType.NATIVE))
    }

    companion object {
        fun create(fragment: Fragment, callbacks: CloudAuthenticator.Callbacks): YandexAuthenticator {
            return YandexAuthenticator(fragment, callbacks)
        }
    }

}

