package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.YandexAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentAuthEditBinding
import com.github.aakumykov.sync_dir_to_cloud.ext_functions.showToast
import com.github.aakumykov.sync_dir_to_cloud.view.view_utils.ValidationResult
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import com.yandex.authsdk.internal.strategy.LoginType

// FIXME: переименовать в CreateAuthFragment
class AuthEditFragment : DialogFragment(R.layout.fragment_auth_edit),
    CloudAuthenticator.Callbacks
{
    private var _binding: FragmentAuthEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var yandexAuthenticator: YandexAuthenticator

    private var authToken: String? = null
    private var authName: String? = null

    private val viewModel: AuthEditViewModel by viewModels()


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


        authToken = savedInstanceState?.getString(AUTH_TOKEN)
        authToken?.let { binding.tokenView.text = it }

        authName = savedInstanceState?.getString(AUTH_NAME)
        authName?.let { binding.authNameView.setText(it) }


        viewModel.nameValidationResult.observe(viewLifecycleOwner, this::onNameCheckChanged)
        viewModel.tokenValidationResult.observe(viewLifecycleOwner, this::onTokenCheckChanged)


        binding.yandexAuthButton.setOnClickListener {
            yandexAuthenticator.startAuth()
        }

        binding.googleAuthButton.setOnClickListener {
            showToast(R.string.not_implemented_yet)
        }

        binding.buttonsInclude.saveButton.setOnClickListener { saveCloudAuth() }

        binding.buttonsInclude.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun onNameCheckChanged(nameValidationResult: ValidationResult?) {
        nameValidationResult?.error?.let { errorMessage ->
            binding.authNameView.error = errorMessage.get(requireContext())
        }
    }

    private fun onTokenCheckChanged(tokenValidationResult: ValidationResult?) {
        tokenValidationResult?.error?.let { errorMessage ->
            binding.errorView.text = errorMessage.get(requireContext())
        } ?: {
            binding.errorView.text = ""
        }
    }

    private fun saveCloudAuth() {

        if (null == authToken) {
            showToast("Авторизуйтесь в облаке")
            return
        }

        authName = binding.authNameView.text.toString()

        authName?.let {
            viewModel.createCloudAuth(authName!!, authToken!!)
            dismiss()
        } ?: binding.authNameView.setError("Не может быть пустым")
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        authToken?.let { outState.putString(AUTH_TOKEN, it) }
        authName?.let { outState.putString(AUTH_NAME, it) }
    }


    override fun onCloudAuthSuccess(authToken: String) {
        this.authToken = authToken
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
        const val AUTH_NAME = "AUTH_NAME"
    }
}