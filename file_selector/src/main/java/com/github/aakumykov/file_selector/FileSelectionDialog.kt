package com.github.aakumykov.file_selector

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.aakumykov.cloud_file_lister.file_lister.DirItem
import com.github.aakumykov.cloud_file_lister.file_lister.FSItem
import com.github.aakumykov.cloud_file_lister.file_lister.FileLister
import com.github.aakumykov.cloud_file_lister.file_lister.ParentDirItem
import com.github.aakumykov.cloud_file_lister.file_lister.RootDirItem
import com.github.aakumykov.file_selector.databinding.DialogFileSelectorBinding
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import kotlin.concurrent.thread

abstract class FileSelectionDialog : DialogFragment(), DefaultLifecycleObserver,
    AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private var _binding: DialogFileSelectorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FileSelectionViewModel by viewModels()

    private var firstRun: Boolean = true
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private val itemList: MutableList<FSItem> = mutableListOf()
    private lateinit var listAdapter: FileListAdapter

    private var callback: Callback? = null


    abstract fun fileLister(): FileLister


    fun show(fragmentManager: FragmentManager): FileSelectionDialog {
        show(fragmentManager, TAG)
        return this
    }


    fun setCallback(callback: Callback): FileSelectionDialog {
        this.callback = callback
        return this
    }

    fun unsetCallback() {
        this.callback = null
    }


    override fun onStart(owner: LifecycleOwner) {
        super<DefaultLifecycleObserver>.onStart(owner)
        callback?.let { setCallback(it) }
    }

    override fun onStop(owner: LifecycleOwner) {
        super<DefaultLifecycleObserver>.onStop(owner)
        this.callback = null
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogFileSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstRun = (null == savedInstanceState)

        viewModel.fileList.observe(viewLifecycleOwner, ::onFileListChanged)
        viewModel.selectedList.observe(viewLifecycleOwner, ::onSelectionListChanged)
        viewModel.currentPath.observe(viewLifecycleOwner, ::onCurrentPathChanged)
        viewModel.errorMessage.observe(viewLifecycleOwner, ::onErrorChanged)

        listAdapter = FileListAdapter(requireContext(), R.layout.file_list_item, R.id.titleView, itemList)

        binding.listView.adapter = listAdapter

        binding.listView.onItemClickListener = this
        binding.listView.onItemLongClickListener = this

        binding.confirmSelectionButton.setOnClickListener { onConfirmSelectionClicked() }
    }

    private fun onConfirmSelectionClicked() {
        callback?.onConfirmSelectionClicked(viewModel.getSelectedList())
        dismiss()
    }

    private fun onSelectionListChanged(selectionList: List<FSItem>?) {
        selectionList?.let { list ->
            listAdapter.updateSelections(list)
            binding.confirmSelectionButton.isEnabled = list.isNotEmpty()
        }
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = itemList[position]
        if (item is DirItem) openDir(item)
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ): Boolean {
        viewModel.toggleInSelectionList(itemList[position])
        return true
    }


    private fun openDir(fsItem: DirItem) {

        hideError()
        showProgressBar()

        thread {
            try {
                val list = fileLister().openAndListDir(fsItem)

                handler.post {
                    hideProgressBar()
                    viewModel.clearSelectionList()
                    viewModel.setFileList(list)
                    viewModel.setCurrentPath(fileLister().getCurrentPath())
                }
            }
            catch (throwable: Throwable) {
                handler.post { viewModel.setError(throwable) }
            }
            finally {
                handler.post { hideProgressBar() }
            }
        }
    }


    private fun onErrorChanged(throwable: Throwable?) {
        throwable?.let {
            showError(throwable)
            Log.e(TAG, ExceptionUtils.getErrorMessage(throwable), throwable)
        }
    }


    private fun onCurrentPathChanged(path: String?) {
        binding.pathView.text = path?.let { path } ?: "?"
    }


    private fun onFileListChanged(list: List<FSItem>?) {
        list?.let {
            hideProgressBar()

            itemList.clear()
            itemList.add(ParentDirItem())
            itemList.addAll(it)
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    override fun onStart() {
        super<DialogFragment>.onStart()

        if (firstRun)
            openDir(RootDirItem())
    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }


    private fun showError(throwable: Throwable) {
        showError(ExceptionUtils.getErrorMessage(throwable))
    }
    private fun showError(text: String) {
        binding.errorView.text = text
    }
    private fun hideError() {
        binding.errorView.text = ""
    }


    interface Callback {
        fun onConfirmSelectionClicked(selectedItemsList: List<FSItem>)
    }


    /*private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }*/


    companion object {
        val TAG: String = FileSelectionDialog::class.java.simpleName
        const val AUTH_TOKEN = "AUTH_TOKEN"

        fun find(fragmentManager: FragmentManager): FileSelectionDialog? {
            return when(val fragment = fragmentManager.findFragmentByTag(TAG)) {
                is FileSelectionDialog -> fragment
                else -> null
            }
        }
    }
}