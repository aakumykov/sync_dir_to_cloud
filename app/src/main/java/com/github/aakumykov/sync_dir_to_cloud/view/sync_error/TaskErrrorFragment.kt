package com.github.aakumykov.sync_dir_to_cloud.view.sync_error

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.Constants
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskErrorBinding

class TaskErrrorFragment : Fragment(R.layout.fragment_task_error) {

    /*private val menuItems = arrayOf(
        CustomMenuItem(
            id = R.id.actionAppProperties,
            title = R.string.MENU_ITEM_app_properties,
            icon = R.drawable.ic_app_properties,
            action = { activity?.openAppProperties() }
        ),
        CustomMenuItem(
            id = R.id.actionManageExternalStorage,
            title = R.string.MENU_ITEM_manage_external_storage,
            icon = R.drawable.ic_storage,
            action = { activity?.also { StorageAccessHelper.openStorageAccessSettings(this) } }
        ),
        CustomMenuItem(
            id = R.id.actionAppSettings,
            title = R.string.MENU_ITEM_manage_external_storage,
            icon = R.drawable.ic_app_settings,
            action = { activity?.also { onAppSettingsClicked() } }
        )
    )*/

    private var _binding: FragmentTaskErrorBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTaskErrorBinding.bind(view)
        requireActivity().intent?.also { displayDataFromIntent(it) } ?: run { showNoIntentError() }
    }

    private fun showNoIntentError() {
        binding.errorTitle.setText(R.string.error_there_is_no_data_to_display)
    }

    private fun displayDataFromIntent(intent: Intent) {
        binding.errorDetails.text = intent.getStringExtra(Constants.ERROR_DETAILS)
        binding.errorTitle.text = intent.getStringExtra(Constants.ERROR_TITLE)
    }


    override fun onResume() {
        super.onResume()
//        menuStateViewModel.sendMenuState(MenuState(*menuItems))
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun create(intent: Intent): TaskErrrorFragment {
            return TaskErrrorFragment().apply {
                arguments = intent.extras
            }
        }
    }
}