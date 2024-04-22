package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit_2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.file_lister_navigator_selector.extensions.hide
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.databinding.DialogCloudAuthEditBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.showToast
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class CloudAuthEditDialog : DialogFragment(R.layout.dialog_cloud_auth_edit),
    CloudAuthenticator.Callbacks
{
    private var _binding: DialogCloudAuthEditBinding? = null
    private val binding get() = _binding!!

    private var cloudAuthenticator: CloudAuthenticator? = null
    private var cloudAuthToken: String? = null

    private lateinit var viewModel: CloudAuthEditViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogCloudAuthEditBinding.bind(view)

        prepareLayout()
        restoreAuthToken(savedInstanceState)
        prepareButtons()
        prepareCloudAuthenticator()
        prepareViewModel()
    }

    private fun prepareLayout() {
        binding.cloudAuthButton.setText(authButtonLabel())
    }

    private fun prepareViewModel() {
        // Удивительно, но ViewModel создаётся только один раз, а не при каждом
        // пересоздании фрагмента, несмотря на то, что ViewModelStoreOwner - this ...
        viewModel = DaggerViewModelHelper.get(this, CloudAuthEditViewModel::class.java)

        viewModel.authCreationResult.observe(viewLifecycleOwner, ::onResultChanged)
    }

    private fun onResultChanged(result: Result<CloudAuth>?) {
        result?.also {
            if (it.isSuccess)
                closeDialog()
            else {
                showError(it.exceptionOrNull())
                hideProgressBar()
                enableForm()
            }
        }
    }

    private fun showError(throwable: Throwable?) {
        binding.errorView.apply {
            text = throwable?.let {
                ExceptionUtils.getErrorMessage(throwable)
            } ?: getString(R.string.unknown_error)
            visibility = View.VISIBLE
        }
    }

    private fun hideError() {
        binding.errorView.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        storeAuthToken(outState)
    }

    private fun restoreAuthToken(savedInstanceState: Bundle?) {
        cloudAuthToken = savedInstanceState?.getString(AUTH_TOKEN)
    }

    private fun storeAuthToken(outState: Bundle) {
        outState.putString(AUTH_TOKEN, cloudAuthToken)
    }

    private fun prepareCloudAuthenticator() {
        cloudAuthenticator = storageType()?.let {
            App.getAppComponent()
                .getCloudAuthenticatorFactoryAssistedFactory()
                .createCloudAuthenticatorFactory(this, this)
                .createCloudAuthenticator(it)
        }
    }

    private fun prepareButtons() {
        binding.cloudAuthButton.setOnClickListener { onCloudAuthButtonClicked() }
        binding.saveCancelButtonsInclude.saveButton.setOnClickListener { onSaveButtonClicked() }
        binding.saveCancelButtonsInclude.cancelButton.setOnClickListener { closeDialog() }
    }

    private fun closeDialog() {
        dismiss()
    }

    private fun onSaveButtonClicked() {

        hideError()
        disableForm()
        showProgressBar()

        cloudAuthToken?.also { token ->
            viewModel.createCloudAuth(
                binding.nameView.text.toString(),
                token
            )
        }
    }

    private fun disableForm() {
        binding.nameView.isEnabled = false
        binding.cloudAuthButton.isEnabled = false
        binding.saveCancelButtonsInclude.saveButton.isEnabled = false
    }

    private fun enableForm() {
        binding.nameView.isEnabled = true
        binding.cloudAuthButton.isEnabled = true
        binding.saveCancelButtonsInclude.saveButton.isEnabled = true
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun onCloudAuthButtonClicked() {
        cloudAuthenticator?.startAuth()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun storageType(): StorageType? {
        return try {
            arguments?.getString(STORAGE_TYPE)?.let {
                StorageType.valueOf(it)
            }
        } catch (e: Exception) {
            Log.e(TAG, ExceptionUtils.getErrorMessage(e), e)
            null
        }
    }

    @StringRes
    private fun authButtonLabel(): Int {
        return arguments?.getInt(AUTH_BUTTON_LABEL) ?: R.string.BUTTON_LABEL_unknown_storage_type
    }

    companion object {
        val TAG: String = CloudAuthEditDialog::class.java.simpleName

        const val STORAGE_TYPE = "STORAGE_TYPE"
        const val AUTH_BUTTON_LABEL = "AUTH_BUTTON_LABEL"
        const val AUTH_TOKEN = "AUTH_TOKEN"

        fun create(storageType: StorageType, authButtonLabel: Int): CloudAuthEditDialog {
            return CloudAuthEditDialog().apply {
                arguments = bundleOf(
                    STORAGE_TYPE to storageType.name,
                    AUTH_BUTTON_LABEL to authButtonLabel
                )
            }
        }
    }

    override fun onCloudAuthSuccess(authToken: String) {
        cloudAuthToken = authToken
    }

    override fun onCloudAuthFailed(throwable: Throwable) {
        showToast(R.string.auth_error)
        Log.e(TAG, ExceptionUtils.getErrorMessage(throwable), throwable)
    }
}