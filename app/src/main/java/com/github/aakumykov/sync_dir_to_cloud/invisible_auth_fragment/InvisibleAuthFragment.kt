package com.github.aakumykov.sync_dir_to_cloud.invisible_auth_fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticator
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdkContract
import com.yandex.authsdk.internal.strategy.LoginType

abstract class InvisibleAuthFragment : DialogFragment(), CloudAuthenticator {

    abstract fun setSuccessAuthResult(authToken: String)

    abstract fun setErrorAuthResult(errorMsg: String)

    companion object {
        val TAG: String = InvisibleAuthFragment::class.java.simpleName
        const val KEY_AUTH_RESULT = "KEY_AUTH_RESULT"
        const val AUTH_TOKEN = "AUTH_TOKEN"
        const val AUTH_ERROR_MSG = "AUTH_ERROR_MSG"
    }
}


class YandexInvisibleAuthFragment : InvisibleAuthFragment(), CloudAuthenticator.Callbacks {

    private lateinit var activityResultLauncher: ActivityResultLauncher<YandexAuthLoginOptions>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yandexAuthOptions = YandexAuthOptions(requireContext(), true)
        val yandexAuthSdkContract = YandexAuthSdkContract(yandexAuthOptions)

        activityResultLauncher = registerForActivityResult(yandexAuthSdkContract) { result ->
            result.getOrNull()?.also {
                this.onCloudAuthSuccess(it.value)
            } ?: this.onCloudAuthFailed(Throwable("Auth was cancelled or result is null."))
        }
    }

    override fun setSuccessAuthResult(authToken: String) {
        setFragmentResult(KEY_AUTH_RESULT, bundleOf(
            AUTH_TOKEN to authToken
        ))
        // TODO: вызывать закрытие централизованно
        dismiss()
    }

    override fun setErrorAuthResult(errorMsg: String) {
        setFragmentResult(KEY_AUTH_RESULT, bundleOf(
            AUTH_ERROR_MSG to errorMsg
        ))
        dismiss()
    }

    override fun startAuth() {
        activityResultLauncher.launch(YandexAuthLoginOptions(LoginType.NATIVE))
    }

    override fun onCloudAuthSuccess(authToken: String) {
        setSuccessAuthResult(authToken)
    }

    override fun onCloudAuthFailed(throwable: Throwable) {
        setErrorAuthResult(ExceptionUtils.getErrorMessage(throwable))
        Log.e(TAG, ExceptionUtils.getErrorMessage(throwable), throwable)
    }

    companion object {
        fun create(): CloudAuthenticator = YandexInvisibleAuthFragment()
        val TAG: String = YandexInvisibleAuthFragment::class.java.simpleName
    }
}