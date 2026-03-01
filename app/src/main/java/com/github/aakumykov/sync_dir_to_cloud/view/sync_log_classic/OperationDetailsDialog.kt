package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.file_lister_navigator_selector.utils.DateFormatter
import com.github.aakumykov.sync_dir_to_cloud.Constants
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.DialogOperationDetailsBinding
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.extensions.makeGone
import com.github.aakumykov.sync_dir_to_cloud.extensions.makeVisible
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.isError
import kotlinx.coroutines.launch

/**
 * Вызывающий код обязан предоставить аргументы 
 * [OperationDetailsDialog.KEY_LOG_ITEM_ABOUT],
 * [OperationDetailsDialog.KEY_ORIG_LOG_ID].
 */
class OperationDetailsDialog : DialogFragment(R.layout.dialog_operation_details) {
    
    private lateinit var viewModel: OperationDetailsDialogViewModel

    private var _binding: DialogOperationDetailsBinding? = null
    private val binding get() = _binding!!

    private val origLogId: String
        get() =arguments?.getString(KEY_ORIG_LOG_ID)!!

    private val logItemAbout: LogItemAbout
        get() = arguments?.getString(KEY_LOG_ITEM_ABOUT)!!
            .let { LogItemAbout.valueOf(it) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareViewModel(null != savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogOperationDetailsBinding.inflate(layoutInflater)

        return AlertDialog.Builder(requireContext())
            .setTitle("$logItemAbout / $origLogId")
            .setView(binding.root)
            .setNeutralButton(R.string.DIALOG_BUTTON_close) { dialog, which ->
                dismiss()
            }
            .create()
    }

    private fun prepareViewModel(isFirstRun: Boolean) {

        viewModel = DaggerViewModelHelper.get(this,
            OperationDetailsDialogViewModel::class.java)

        lifecycleScope.launch {
            if (isFirstRun)
                viewModel.startWorking(logItemAbout, origLogId)
            viewModel.logOfSync.collect(::onLogOfSyncChanged)
        }
    }

    private suspend fun onLogOfSyncChanged(logOfSync: LogOfSync?) {
        logOfSync?.also { fillViewWithData(logOfSync) }
            ?: run { fillViewWithError(R.string.data_not_found) }
    }

    private fun fillViewWithError(@StringRes errorMessageId: Int) {
        binding.apply {
            errorView.text = resources.getString(errorMessageId)
            hideAllItems()
            showErrorItem()
        }
    }

    private fun hideAllItems() {
        binding.apply {
            text.makeGone()
            subText.makeGone()
            errorView.makeGone()
            date.makeGone()
        }
    }

    private fun showAllItems() {
        binding.apply {
            text.makeVisible()
            subText.makeVisible()
            errorView.makeVisible()
            date.makeVisible()
        }
    }

    private fun showErrorItem() = binding.errorView.makeVisible()
    private fun hideErrorItem() = binding.errorView.makeGone()

    private fun fillViewWithData(logOfSync: LogOfSync) {
        binding.apply {
            showTextIfNotNull(logOfSync.text, text)

            showTextIfNotNull(logOfSync.subText, text)

            showTextIfNotNull(
                DateFormatter.humanReadableDate(logOfSync.startTime),
                text)
        }

        colorizeSubText(logOfSync)
    }

    private fun showTextIfNotNull(someText: String?, textView: TextView) {
        someText?.also {
            textView.text = it
            textView.makeVisible()
        } ?: run {
            textView.text = Constants.EMPTY_STRING
            textView.makeGone()
        }
    }

    private fun colorizeSubText(logOfSync: LogOfSync) {
        binding.subText.setTextColor(
            (if (logOfSync.isError) R.color.error
            else android.R.color.tab_indicator_text).let {
                resources.getColor(it, null)
            }
        )
    }

    companion object {
        val TAG: String = OperationDetailsDialog::class.java.name

        const val KEY_ORIG_LOG_ID = "ORIG_LOG_ID"
        const val KEY_LOG_ITEM_ABOUT = "LOG_ITEM_ABOUT"

        fun create(origLogId: String, logItemAbout: LogItemAbout): OperationDetailsDialog {
            return OperationDetailsDialog().apply {
                arguments = bundleOf(
                    KEY_ORIG_LOG_ID to origLogId,
                    KEY_LOG_ITEM_ABOUT to logItemAbout.name
                )
            }
        }
    }
}