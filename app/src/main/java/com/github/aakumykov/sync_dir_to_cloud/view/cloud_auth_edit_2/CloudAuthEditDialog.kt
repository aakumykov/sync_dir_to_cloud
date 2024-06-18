package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit_2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.factories.cloud_auth.CloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.databinding.DialogCloudAuthEditBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.utils.WebViewChecker
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list.StorageTypeIconProvider
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.showToast
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils

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

        restoreAuthToken(savedInstanceState)
        hideAuthButtonIfAuthorized()

        prepareLayout()
        prepareButtons()
        prepareCloudAuthenticator()
        prepareViewModel()

        // Вызывать после "prepareCloudAuthenticator()".
        processRequestedStorageType()
    }

    private fun processRequestedStorageType() {
        if (StorageType.LOCAL == storageType()) {
            cloudAuthenticator?.startAuth()
        }
    }

    override fun onCloudAuthSuccess(authToken: String) {
        cloudAuthToken = authToken
        hideAuthButtonIfAuthorized()
    }

    private fun hideAuthButtonIfAuthorized() {
        binding.authRequestButton.visibility = if (null != cloudAuthToken) View.GONE else View.VISIBLE
    }

    override fun onCloudAuthFailed(throwable: Throwable) {
        showToast(R.string.auth_error)
        Log.e(TAG, ExceptionUtils.getErrorMessage(throwable), throwable)
    }

    private fun prepareLayout() {
        binding.storageTypeView.setImageResource(StorageTypeIconProvider.getIconFor(storageType()))
        binding.authRequestButton.text = authButtonLabel()
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
        cloudAuthenticator = storageType().let {
            App.getAppComponent()
                .getCloudAuthenticatorFactoryAssistedFactory()
                .createCloudAuthenticatorFactory(this, this)
                .createCloudAuthenticator(it)
        }
    }

    private fun prepareButtons() {
        binding.authRequestButton.setOnClickListener { onCloudAuthButtonClicked() }
        binding.saveCancelButtonsInclude.saveButton.setOnClickListener { onSaveButtonClicked() }
        binding.saveCancelButtonsInclude.cancelButton.setOnClickListener { closeDialog() }
    }

    private fun closeDialog() {
        dismiss()
    }

    private fun onSaveButtonClicked() {
        cloudAuthToken?.also { token ->
            hideError()
            disableForm()
            showProgressBar()
            viewModel.createCloudAuth(
                binding.nameView.text.toString(),
                storageType(),
                token
            )
        }
    }

    private fun disableForm() {
        binding.nameView.isEnabled = false
        binding.authRequestButton.isEnabled = false
        binding.saveCancelButtonsInclude.saveButton.isEnabled = false
    }

    private fun enableForm() {
        binding.nameView.isEnabled = true
        binding.authRequestButton.isEnabled = true
        binding.saveCancelButtonsInclude.saveButton.isEnabled = true
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun onCloudAuthButtonClicked() {
        if (WebViewChecker.isAvailable(requireContext()))
            cloudAuthenticator?.startAuth()
        else
            reportWebViewNowAvailable()
    }

    private fun reportWebViewNowAvailable() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.DIALOG_WEBVIEW_NOT_AVAILABLE_title)
            setMessage(R.string.DIALOG_WEBVIEW_NOT_AVAILABLE_message)
            setNeutralButton(R.string.DIALOG_WEBVIEW_NOT_AVAILABLE_neutral_button) { _,_ -> }
        }.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun storageType(): StorageType {
        return arguments?.getString(STORAGE_TYPE)?.let {
            StorageType.valueOf(it)
        } ?: throw IllegalArgumentException("Fragment arguments must contain '${STORAGE_TYPE}' value.")
    }

    private fun authButtonLabel(): String {
        return arguments?.getString(AUTH_BUTTON_LABEL) ?: getString(R.string.BUTTON_LABEL_unknown_storage_type)
    }

    companion object {
        val TAG: String = CloudAuthEditDialog::class.java.simpleName

        const val STORAGE_TYPE = "STORAGE_TYPE"
        const val AUTH_BUTTON_LABEL = "AUTH_BUTTON_LABEL"
        const val AUTH_TOKEN = "AUTH_TOKEN"

        fun create(storageType: StorageType, authButtonLabel: String): CloudAuthEditDialog {
            return CloudAuthEditDialog().apply {
                arguments = bundleOf(
                    STORAGE_TYPE to storageType.name,
                    AUTH_BUTTON_LABEL to authButtonLabel
                )
            }
        }
    }
}