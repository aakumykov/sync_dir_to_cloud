package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentAuthListBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit.AuthEditFragment
import com.github.aakumykov.sync_dir_to_cloud.view.task_edit.CloudAuthSetter
import com.github.aakumykov.sync_dir_to_cloud.view.utils.ListViewAdapter
import kotlinx.coroutines.launch

class AuthListFragment : DialogFragment(R.layout.fragment_auth_list) {

    private var _binding: FragmentAuthListBinding? = null
    private val binding get() = _binding!!

    private lateinit var listAdapter: ListViewAdapter<CloudAuth>
    private val viewModel: AuthListViewModel by viewModels()

    private lateinit var cloudAuthSetter: CloudAuthSetter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareLayout(view)
        prepareButtons()
        prepareViewModel()
        prepareListView()

        cloudAuthSetter = App.getAppComponent().getCloudAuthSetter()
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

        viewModel.authList.observe(viewLifecycleOwner) { authList ->
            listAdapter.setList(authList)
            hideProgressShowList()
        }

        lifecycleScope.launch {
            viewModel.startLoadingList()
        }
    }


    private fun prepareListView() {

        listAdapter = ListViewAdapter<CloudAuth>(
            requireContext(),
            R.layout.auth_list_item,
            R.id.cloudAuthNameView,
            ArrayList()
        )

        binding.listView.adapter = listAdapter

        binding.listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            listAdapter.getItem(position)?.let {

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
        val TAG: String = AuthListFragment::class.java.simpleName
    }
}