package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.YandexDiskAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.databinding.ActivityAuthListBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth

class AuthListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthListBinding
    private lateinit var listAdapter: AuthAdapter
    private val viewModel: AuthListViewModel by viewModels()
    private var currentCloudAuthenticator: CloudAuthenticator? = null
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.context = this

        binding = ActivityAuthListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setTitle(R.string.AUTH_LIST_ACTIVITY_title)

        binding.addButton.setOnClickListener { onAdButtonClicked() }

        listAdapter = AuthAdapter(this, android.R.layout.simple_list_item_1)
        binding.listView.adapter = listAdapter
        binding.listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val text = "id: $id, position: $position"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }

        viewModel.getAuthList().observe(this) { onAuthListChanged(it) }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        currentCloudAuthenticator?.processCloudAuthResult(requestCode, resultCode, data)
    }



    private fun onAdButtonClicked() {

        val cloudTypes = resources.getStringArray(R.array.cloud_types)

        AlertDialog.Builder(this)
            .setTitle(R.string.DIALOG_AUTH_TYPE_title)
            .setItems(R.array.cloud_types) { _, which ->
                onCloudTypeSelected(cloudTypes[which])
            }
            .create()
            .show()
    }

    private fun onCloudTypeSelected(cloudTypeName: String) {
        currentCloudAuthenticator = when(cloudTypeName) {
            getString(R.string.CLOUD_TYPE_NAME_yandex_disk) ->
                YandexDiskAuthenticator(this, YANDEX_DISK_AUTH_REQUEST_CODE, object: CloudAuthenticator.Callbacks {
                    override fun onCloudAuthSuccess(authToken: String) {
                        Toast.makeText(context, "Авторизация в $cloudTypeName успешна", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCloudAuthFailed(errorMsg: String) {
                        Toast.makeText(context, "Ошибка авторизации в ${cloudTypeName}:\n${errorMsg}", Toast.LENGTH_SHORT).show()
                    }
                })
//            getString(R.string.CLOUD_TYPE_NAME_google_drive) -> {  }
            else -> null
        }
        currentCloudAuthenticator?.startCloudAuth()
    }



    private fun onAuthListChanged(list: List<CloudAuth>) {
        listAdapter.clear()
        for (auth in list) {
            listAdapter.add(auth)
        }
    }



    class AuthAdapter(context: Context, private val itemLayout: Int) : ArrayAdapter<CloudAuth>(context, itemLayout) {
        private var inflater: LayoutInflater

        init {
            inflater = LayoutInflater.from(context)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val view = inflater.inflate(itemLayout, parent, false)
            val cloudAuth = getItem(position)

            cloudAuth?.name.let {
                (view.findViewById(android.R.id.text1) as TextView).text = it
            }

            return view
        }
    }

    companion object {
        const val YANDEX_DISK_AUTH_REQUEST_CODE = 1000
    }
}