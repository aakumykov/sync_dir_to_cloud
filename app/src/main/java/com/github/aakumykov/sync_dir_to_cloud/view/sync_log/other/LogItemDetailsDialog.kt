package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.other

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime

class LogItemDetailsDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val alertDialog = AlertDialog.Builder(requireContext())
            .setNegativeButton(R.string.LOG_ITEM_DETAILS_DIALOG_close_button) { _,_ -> }

        arguments?.getParcelable<SyncObjectLogItem>(SYNC_OBJECT_LOG_ITEM)?.also { logItem ->
            alertDialog.setView(buildInfoView(logItem))
            alertDialog.setTitle(logItem.operationName)
        } ?: {
            alertDialog.setView(buildErrorView(R.string.LOG_ITEM_DETAILS_DIALOG_no_log_item_supplied))
            alertDialog.setTitle(R.string.LOG_ITEM_DETAILS_DIALOG_title)
        }

        return alertDialog.create()
    }

    private fun buildInfoView(logItem: SyncObjectLogItem): View {
        return requireActivity().layoutInflater
            .inflate(R.layout.dialog_log_item_details, null, false)
            .apply {
                findViewById<TextView>(R.id.syncLogItemDetailsId).text = logItem.id
//                findViewById<TextView>(R.id.syncLogItemDetailsMessage).text = logItem.operationName
                findViewById<TextView>(R.id.syncLogItemDetailsItemName).text = getString(R.string.LOG_ITEM_DETAILS_DIALOG_item_name_quotes, logItem.itemName)
                findViewById<TextView>(R.id.syncLogItemDetailsTime).text = CurrentDateTime.format(logItem.timestamp)

                val errorView = findViewById<TextView>(R.id.syncLogItemError)
                logItem.errorMessage?.also {
                    errorView.text = getString(R.string.LOG_ITEM_DETAILS_DIALOG_error_with_details, it)
                    errorView.visibility = View.VISIBLE
                } ?: {
                    errorView.text = null
                    errorView.visibility = View.GONE
                }
            }
    }

    private fun buildErrorView(@StringRes errorMsgRes: Int): View {
        return requireActivity().layoutInflater
            .inflate(R.layout.error_view, null, false)
            .apply {
                findViewById<TextView>(R.id.errorView).setText(errorMsgRes)
            }
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }

    companion object {
        val TAG: String = LogItemDetailsDialog::class.java.simpleName

        const val SYNC_OBJECT_LOG_ITEM = "SYNC_OBJECT_LOG_ITEM"

        fun create(syncObjectLogItem: SyncObjectLogItem): LogItemDetailsDialog {
            return LogItemDetailsDialog().apply {
                arguments = bundleOf(
                    SYNC_OBJECT_LOG_ITEM to syncObjectLogItem
                )
            }
        }
    }
}