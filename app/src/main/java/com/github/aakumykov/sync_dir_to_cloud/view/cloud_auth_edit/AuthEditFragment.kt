package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_auth.StorageAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_auth.YandexAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentAuthEditBinding
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.setError
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.setText
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.showToast
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils


// FIXME: переименовать в CreateAuthFragment
class AuthEditFragment : DialogFragment(R.layout.fragment_auth_edit),
    StorageAuthenticator.Callbacks,
    AdapterView.OnItemSelectedListener
{
    private var _binding: FragmentAuthEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthEditViewModel by viewModels()

    private var storageAuthenticator: StorageAuthenticator? = null

    @Deprecated("Перехожу на StorageAuthenticator")
    private lateinit var yandexAuthenticator: YandexAuthenticator

    private val storageTypeNames: List<String> by lazy {
        listOf(
            getString(R.string.SPINNER_storage_type_default_value),
            *StorageType.entries.map { it.name }.toTypedArray(),
        )
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        yandexAuthenticator = YandexAuthenticator.create(this, this)
    }

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareLayout(view)
        prepareFragmentResultListeners()
        prepareStorageTypeSpinner()
        prepareButtons()
        prepareViewModel()

        // FIXME: если есть ViewModel, можно хранить в ней
        restoreFormValues(savedInstanceState)
    }

    private fun prepareFragmentResultListeners() {
        /*listenForFragmentResult(InvisibleAuthFragment.KEY_AUTH_RESULT) { key,resultBundle ->
            // TODO: искапсулировать получение значений из bundle в InvisibleAuthFragment
            when {
                resultBundle.containsKey(InvisibleAuthFragment.AUTH_TOKEN) -> processAuthToken(
                    resultBundle.getString(InvisibleAuthFragment.AUTH_TOKEN)
                )

                resultBundle.containsKey(InvisibleAuthFragment.AUTH_ERROR_MSG) -> showAuthError(
                    resultBundle.getString(InvisibleAuthFragment.AUTH_ERROR_MSG)
                )

                else -> throw IllegalStateException("Fragment result for InvisibleAuthFragment does not contains any known keys.")
            }
        }*/
    }

    private fun processAuthToken(token: String?) {
        TODO("Not yet implemented")
    }

    // FIXME: нормально показывать эту ошибку
    private fun showAuthError(errorMsg: String?) {
        binding.tokenErrorView.text = errorMsg
    }

    private fun prepareStorageTypeSpinner() {
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, storageTypeNames).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.storageTypeSpinner.apply {
                setAdapter(adapter)
                setOnItemSelectedListener(this@AuthEditFragment)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun prepareButtons() {

        binding.cloudAuthButton.setOnClickListener { onCloudAuthClicked() }

        binding.yandexAuthButton.setOnClickListener {
            yandexAuthenticator.startAuth()
        }

        binding.googleAuthButton.setOnClickListener {
            showToast(com.github.aakumykov.sync_dir_to_cloud.R.string.not_implemented_yet)
        }

        /*binding.saveCancelButtonsInclude.saveButton.setOnClickListener {

            hideTokenError()

            storageType()?.also { storageType ->
                viewModel.createCloudAuth(
                    binding.nameView.text.toString(),
                    storageType,
                    binding.tokenView.text.toString()
                )
            } ?: showStorageTypeNotSelectedError()
        }*/

        /*binding.saveCancelButtonsInclude.cancelButton.setOnClickListener {
            dismiss()
        }*/
    }

    private fun onCloudAuthClicked() {
        /*storageType()?.also { storageType ->

            if (StorageType.LOCAL != storageType) {
                cloudAuthenticatorFactory.createCloudAuthenticator(storageType).apply {
                    (this as InvisibleAuthFragment).show(childFragmentManager, InvisibleAuthFragment.TAG)
                }
            }

        } ?: showStorageTypeNotSelectedError()*/
    }

    private fun showStorageTypeNotSelectedError() {
        showToast(R.string.ERROR_select_storage_type)
    }

    private fun storageType(): StorageType? {
        return binding.storageTypeSpinner.selectedItemPosition.let { position ->
            if (0 == position) null
            else StorageType.valueOf(storageTypeNames[position])
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

        showToast(com.github.aakumykov.sync_dir_to_cloud.R.string.auth_error)
        MyLogger.e(TAG, ExceptionUtils.getErrorMessage(throwable), throwable)
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