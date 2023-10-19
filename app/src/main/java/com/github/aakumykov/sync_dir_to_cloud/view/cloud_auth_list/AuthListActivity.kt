package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.ActivityAuthListBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.view.utils.ListViewAdapter

class AuthListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthListBinding
    private val viewModel: AuthListViewModel by viewModels()
    private lateinit var listAdapter: ListViewAdapter<CloudAuth>
    // TODO: внедрять через Dagger
//    private val taskEditViewModel: TaskEditViewModel by viewModels()
//    private val cloudAuthSetter: CloudAuthSetter = taskEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareLayout()
        prepareButtons()
        prepareListAdapter()
        prepareViewModels()
    }


    private fun prepareViewModels() {
        viewModel.getAuthList().observe(this, this::onAuthListChanged)

    }

    private fun prepareListAdapter() {
        listAdapter =
            ListViewAdapter<CloudAuth>(
                this,
                R.layout.auth_list_item,
                R.id.cloudAuthNameView,
                ArrayList()
            )

        binding.listView.adapter = listAdapter

        binding.listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            listAdapter.getItem(position)?.let {
                Toast.makeText(this@AuthListActivity, it.name, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun prepareButtons() {
        binding.addButton.setOnClickListener { onAddButtonClicked() }
    }

    private fun prepareLayout() {
        binding = ActivityAuthListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setTitle(R.string.AUTH_LIST_page_title)
    }

    private fun onAuthListChanged(cloudAuths: List<CloudAuth>?) {
        cloudAuths?.let {
            listAdapter.setList(cloudAuths)
        }
    }

    private fun onAddButtonClicked() {
        /*AlertDialog.Builder(this)
            .setTitle()
            .create()
            .show()*/
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}