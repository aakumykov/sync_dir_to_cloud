package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentAuthListBinding
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentAuthListRelativeBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit.AuthEditFragment
import com.github.aakumykov.sync_dir_to_cloud.view.utils.ListViewAdapter
import kotlinx.coroutines.launch

typealias Layout = FragmentAuthListRelativeBinding

class AuthListDialog : DialogFragment(R.layout.fragment_auth_list_relative), AuthSelectionDialog {

    private var _binding: Layout? = null
    private val binding get() = _binding!!

    private lateinit var listAdapter: ListViewAdapter<CloudAuth>

    private val authListViewModel: AuthListViewModel by viewModels()

    private var authSelectionCallback: AuthSelectionDialog.Callback? = null


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
        binding.addButton.setOnClickListener {
            AuthEditFragment().show(childFragmentManager, AuthEditFragment.TAG)
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
            ArrayList()
        )

        binding.listView.adapter = listAdapter

        binding.listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            listAdapter.getItem(position)?.let { cloudAuth ->
                authSelectionCallback?.onCloudAuthSelected(cloudAuth)
                dismiss()
            }
        }
    }


    private fun hideProgressShowList() {
        binding.progressBar.visibility = View.GONE
        binding.listView.visibility = View.VISIBLE
        binding.addButton.visibility = View.VISIBLE
    }

    companion object {
        val TAG: String = AuthListDialog::class.java.simpleName
    }


    override fun setCallback(callback: AuthSelectionDialog.Callback) {
        authSelectionCallback = callback
    }

    override fun unsetCallback() {
        authSelectionCallback = null
    }
}