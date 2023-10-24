package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.YandexAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentAuthEditBinding
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import com.yandex.authsdk.internal.strategy.LoginType

class AuthEditFragment : DialogFragment(R.layout.fragment_auth_edit),
    CloudAuthenticator.Callbacks
{
    private var _binding: FragmentAuthEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var yandexAuthenticator: YandexAuthenticator
    private var currentAuthToken: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        yandexAuthenticator = YandexAuthenticator(
            this,
            LoginType.CHROME_TAB,
            this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAuthEditBinding.bind(view)

        currentAuthToken = savedInstanceState?.getString(AUTH_TOKEN)
        currentAuthToken?.let { binding.tokenView.text = it }

        binding.yandexAuthButton.setOnClickListener {
            yandexAuthenticator.startAuth()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentAuthToken?.let {
            outState.putString(AUTH_TOKEN, it)
        }
    }

    override fun onCloudAuthSuccess(authToken: String) {
        currentAuthToken = authToken
        binding.tokenView.text = authToken
        binding.errorView.text = ""
    }

    override fun onCloudAuthFailed(throwable: Throwable) {
        binding.tokenView.text = ""
        binding.errorView.text = ExceptionUtils.getErrorMessage(throwable)
    }

    companion object {
        val TAG: String = AuthEditFragment::class.java.simpleName
        const val AUTH_TOKEN = "AUTH_TOKEN"
    }
}