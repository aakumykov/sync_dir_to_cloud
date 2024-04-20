package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.core.util.Function
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentAuthListRelativeBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.toJSON
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit.AuthEditFragment
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit_2.CloudAuthEditDialog
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.ListViewAdapter
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch

typealias Layout = FragmentAuthListRelativeBinding

class AuthListDialog : DialogFragment(R.layout.fragment_auth_list_relative) {

    private var _binding: Layout? = null
    private val binding get() = _binding!!

    private lateinit var listAdapter: ListViewAdapter<CloudAuth>
    private val authListViewModel: AuthListViewModel by viewModels()
    private val gson: Gson by lazy { App.getAppComponent().getGson() }

    private val withNextAction: Boolean get() = arguments?.getBoolean(WITH_NEXT_ACTION, false) ?: false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareLayout(view)
        prepareButtons()
        prepareViewModel()
        prepareList()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun prepareLayout(view: View) {
        _binding = Layout.bind(view)
    }


    private fun prepareButtons() {
        binding.addLocalButton.setOnClickListener {
            CloudAuthEditDialog.create(StorageType.LOCAL)
                .show(childFragmentManager, AuthEditFragment.TAG)
        }

        binding.addYandexButton.setOnClickListener {
            CloudAuthEditDialog.create(StorageType.YANDEX_DISK)
                .show(childFragmentManager, AuthEditFragment.TAG)
        }
    }


    private fun prepareViewModel() {

        authListViewModel.authList.observe(viewLifecycleOwner) { authList ->
            listAdapter.setList(authList)
            hideProgressShowList()
        }

        lifecycleScope.launch {
            authListViewModel.startLoadingList()
        }
    }


    private fun prepareList() {

        listAdapter = ListViewAdapter<CloudAuth>(
            requireContext(),
            R.layout.auth_list_item,
            R.id.cloudAuthNameView,
            ArrayList(),
            Function { return@Function it.name }
        )

        binding.listView.adapter = listAdapter

        binding.listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            listAdapter.getItem(position)?.let { cloudAuth ->
                setFragmentResult(KEY_SELECT_CLOUD_AUTH, bundleOf(
                    CLOUD_AUTH to cloudAuth.toJSON(gson),
                    WITH_NEXT_ACTION to withNextAction
                ))
                dismiss()
            }
        }
    }


    private fun hideProgressShowList() {
        binding.progressBar.visibility = View.GONE
        binding.listView.visibility = View.VISIBLE
//        binding.addButton.visibility = View.VISIBLE
    }

    companion object {

        val TAG: String = AuthListDialog::class.java.simpleName
        const val KEY_SELECT_CLOUD_AUTH = "CODE_SELECT_CLOUD_AUTH"
        const val CLOUD_AUTH = "CLOUD_AUTH"
        const val WITH_NEXT_ACTION = "WITH_NEXT_ACTION"

        // TODO: дать более конкретное имя
        fun create(withNextAction: Boolean): AuthListDialog {
            return AuthListDialog().apply {
                arguments = bundleOf(WITH_NEXT_ACTION to withNextAction)
            }
        }

        fun extractCloudAuth(fragmentResult: Bundle): CloudAuth? {
            return fragmentResult.getString(CLOUD_AUTH).let { json ->
                try {
                    Gson().fromJson(json, CloudAuth::class.java)
                } catch (e: JsonSyntaxException) {
                    Log.e(TAG, ExceptionUtils.getErrorMessage(e))
                    null
                }
            }
        }

        fun hasNextAction(fragmentResult: Bundle): Boolean {
            return fragmentResult.getBoolean(WITH_NEXT_ACTION, false)
        }
    }
}