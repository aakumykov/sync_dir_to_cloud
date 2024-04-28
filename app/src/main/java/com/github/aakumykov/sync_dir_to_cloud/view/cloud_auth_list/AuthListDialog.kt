package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import android.content.res.Resources.Theme
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
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
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit_2.CloudAuthEditDialog
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.ListViewAdapter
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.leinardi.android.speeddial.SpeedDialActionItem
import kotlinx.coroutines.launch

typealias Layout = FragmentAuthListRelativeBinding

class AuthListDialog : DialogFragment(R.layout.fragment_auth_list_relative) {

    private var _binding: Layout? = null
    private val binding get() = _binding!!

    private lateinit var listAdapter: ListViewAdapter<CloudAuth>
    private val authListViewModel: AuthListViewModel by viewModels()
    private val gson: Gson by lazy { App.getAppComponent().getGson() }

    private val withNextAction: Boolean get() = arguments?.getBoolean(WITH_NEXT_ACTION, false) ?: false
    private val endpointType: EndpointType? get() = arguments?.getString(ENDPOINT_TYPE)?.let { EndpointType.valueOf(it) }

    /*private fun endpointType(): EndpointType? {
        return arguments?.getString(ENDPOINT_TYPE)?.let {
            return EndpointType.valueOf(it)
        }
    }*/

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
        prepareSpeedDialView()
    }

    // TODO: вынести создание этого меню в отдельный класс
    private fun prepareSpeedDialView() {
        binding.addButton.apply {
            addActionItem(
                SpeedDialActionItem
                    .Builder(R.id.storageTypeLocal, R.drawable.ic_auth_type_local)
                    .setLabel(R.string.speed_dial_auth_label_local)
                    .setFabBackgroundColor(color(R.color.local_storage_color))
                    .setFabImageTintColor(color(R.color.white))
                    .setLabelColor(color(R.color.local_storage_color))
                    .create()
            )
            addActionItem(
                SpeedDialActionItem
                    .Builder(R.id.storageTypeYandex, R.drawable.ic_storage_type_yandex_disk)
                    .setLabel(R.string.speed_dial_auth_label_yandex)
                    .setFabBackgroundColor(color(R.color.yandex_disk_color))
                    .setFabImageTintColor(color(R.color.white))
                    .setLabelColor(color(R.color.yandex_disk_color))
                    .create()
            )
            /*addActionItem(
                SpeedDialActionItem
                    .Builder(R.id.storageTypeGoogle, R.drawable.ic_storage_type_google_drive)
                    .setLabel(R.string.auth_type_google)
                    .setFabBackgroundColor(color(R.color.white))
                    .create()
            )*/
        }
    }

    private fun prepareButtons() {
        binding.addButton.setOnActionSelectedListener {
            binding.addButton.close()
            onSpeedDialActionSelected(it)
        }
    }

    private fun onSpeedDialActionSelected(speedDialActionItem: SpeedDialActionItem?): Boolean {

        val authButtonLabel: AuthButtonLabel by lazy { AuthButtonLabel(resources) }

        when(speedDialActionItem?.id) {
            R.id.storageTypeYandex -> StorageType.YANDEX_DISK
//            R.id.storageTypeGoogle -> StorageType.GOOGLE
            R.id.storageTypeLocal -> StorageType.LOCAL
            else -> null
        }?.also { storageType ->
            CloudAuthEditDialog
                .create(storageType, authButtonLabel.getFor(storageType))
                .show(childFragmentManager, CloudAuthEditDialog.TAG)
        }
        return true
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

        listAdapter = object: ListViewAdapter<CloudAuth>(
            requireContext(),
            R.layout.auth_list_item,
            R.id.cloudAuthNameView,
            R.id.cloudAuthIcon,
            ArrayList(),
            Function { return@Function it.name }
        ) {
            override fun modifyView(item: CloudAuth?, viewHolder: ViewHolder?) {
                item?.let {
                    viewHolder?.iconView?.apply {
                        setImageResource(StorageTypeIconGetter.getIconFor(item.storageType))
                    }
                }
            }
        }

        binding.listView.adapter = listAdapter

        binding.listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            listAdapter.getItem(position)?.let { cloudAuth ->
                onCloudAuthSelected(cloudAuth)
            }
        }
    }

    private fun onCloudAuthSelected(cloudAuth: CloudAuth) {
        setFragmentResult(KEY_SELECT_CLOUD_AUTH, bundleOf(
            CLOUD_AUTH to cloudAuth.toJSON(gson),
            ENDPOINT_TYPE to endpointType?.name, // У Enum спользовать свойство "name".
            WITH_NEXT_ACTION to withNextAction
        ))
        dismiss()
    }


    private fun hideProgressShowList() {
        binding.progressBar.visibility = View.GONE
        binding.listView.visibility = View.VISIBLE
//        binding.addButton.visibility = View.VISIBLE
    }

    companion object {

        val TAG: String = AuthListDialog::class.java.simpleName

        const val KEY_SELECT_CLOUD_AUTH = "CODE_SELECT_CLOUD_AUTH"
        const val KEY_SELECT_SOURCE_AUTH = "CODE_SELECT_SOURCE_AUTH"
        const val KEY_SELECT_TARGET_AUTH = "CODE_SELECT_TARGET_AUTH"

        const val CLOUD_AUTH = "CLOUD_AUTH"
        const val ENDPOINT_TYPE = "ENDPOINT_TYPE"
        const val WITH_NEXT_ACTION = "WITH_NEXT_ACTION"


        // TODO: дать более конкретное имя
        fun create(endpointType: EndpointType, withNextAction: Boolean): AuthListDialog {
            return AuthListDialog().apply {
                arguments = bundleOf(
                    ENDPOINT_TYPE to endpointType.name, // Нужно использовать Enum-поле "name".
                    WITH_NEXT_ACTION to withNextAction,
                )
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

    @ColorInt
    private fun color(@ColorRes colorRes: Int, theme: Theme? = null): Int {
        return ResourcesCompat.getColor(resources, colorRes, theme)
    }
}