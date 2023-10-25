package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.YandexAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentAuthEditBinding
import com.github.aakumykov.sync_dir_to_cloud.view.ext_functions.setError
import com.github.aakumykov.sync_dir_to_cloud.view.ext_functions.setText
import com.github.aakumykov.sync_dir_to_cloud.view.ext_functions.showToast
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import com.yandex.authsdk.internal.strategy.LoginType

// FIXME: переименовать в CreateAuthFragment
class AuthEditFragment : DialogFragment(R.layout.fragment_auth_edit),
    CloudAuthenticator.Callbacks
{
    private var _binding: FragmentAuthEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthEditViewModel by viewModels()
    private lateinit var yandexAuthenticator: YandexAuthenticator


    override fun onAttach(context: Context) {
        super.onAttach(context)

        yandexAuthenticator = YandexAuthenticator(
            this,
            LoginType.CHROME_TAB,
            this)
    }

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareLayout(view)
        prepareButtons()
        prepareViewModel()

        // FIXME: если есть ViewModel, можно хранить в ней
        restoreFormValues(savedInstanceState)
    }

    private fun prepareButtons() {

        binding.yandexAuthButton.setOnClickListener {
            yandexAuthenticator.startAuth()
        }

        binding.googleAuthButton.setOnClickListener {
            showToast(R.string.not_implemented_yet)
        }

        binding.buttonsInclude.saveButton.setOnClickListener {
            hideTokenError()
            viewModel.createCloudAuth(
                binding.nameView.text.toString(),
                binding.tokenView.text.toString()
            )
        }

        binding.buttonsInclude.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun restoreFormValues(savedInstanceState: Bundle?) {
        binding.tokenView.setText(savedInstanceState?.getString(AUTH_TOKEN))
        binding.nameView.setText(savedInstanceState?.getString(AUTH_NAME))
    }

    private fun prepareViewModel() {
        viewModel.formState.observe(viewLifecycleOwner, this::onFormStateChanged)
    }

    private fun prepareLayout(view: View) {
        _binding = FragmentAuthEditBinding.bind(view)
    }


    private fun onFormStateChanged(formState: FormState?) {
        if (null == formState)
            return

        if (formState.isFinished) {
            dismiss()
            return
        }

        formState.nameError?.let {
            binding.nameView.setError(it)
        }

        formState.tokenError?.let {
            binding.tokenErrorView.setText(it)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.tokenView.text.toString().let { outState.putString(AUTH_TOKEN, it) }
        binding.nameView.text.toString().let { outState.putString(AUTH_NAME, it) }
    }


    override fun onCloudAuthSuccess(authToken: String) {
        binding.tokenView.setText(authToken)
        hideTokenError()
    }

    override fun onCloudAuthFailed(throwable: Throwable) {
        binding.tokenView.setText("")
        binding.tokenErrorView.text = ExceptionUtils.getErrorMessage(throwable)

        showToast(R.string.auth_error)
        Log.e(TAG, ExceptionUtils.getErrorMessage(throwable), throwable)
    }

    private fun showTokenError(errorMsg: String) {
        binding.tokenErrorView.text = errorMsg
    }

    private fun hideTokenError() {
        binding.tokenErrorView.text = ""
    }

    companion object {
        val TAG: String = AuthEditFragment::class.java.simpleName
        const val AUTH_TOKEN = "AUTH_TOKEN"
        const val AUTH_NAME = "AUTH_NAME"
    }
}