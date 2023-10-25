package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentAuthListBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit.AuthEditFragment
import com.github.aakumykov.sync_dir_to_cloud.view.utils.ListViewAdapter
import kotlinx.coroutines.launch

class AuthListDialog : DialogFragment(R.layout.fragment_auth_list) {

    private var _binding: FragmentAuthListBinding? = null
    private val binding get() = _binding!!

    private lateinit var listAdapter: ListViewAdapter<CloudAuth>

    // FIXME: зачем их две?
    private val authListViewModel: AuthListViewModel by viewModels()
    private val authSelectionViewModel: AuthSelectionViewModel by activityViewModels()


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
        _binding = FragmentAuthListBinding.bind(view)
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

        binding.listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            listAdapter.getItem(position)?.let { cloudAuth ->
//                selectedCloudAuth.value = cloudAuth
                authSelectionViewModel.setSelectedCloudAuth(cloudAuth)
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
}