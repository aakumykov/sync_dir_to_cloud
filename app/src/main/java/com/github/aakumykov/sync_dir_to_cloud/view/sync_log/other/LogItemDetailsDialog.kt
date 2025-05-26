package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.other

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.adapter.LogOfSync
import kotlinx.parcelize.Parcelize

class LogItemDetailsDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val alertDialog = AlertDialog.Builder(requireContext())
            .setNegativeButton(R.string.LOG_ITEM_DETAILS_DIALOG_close_button) { _,_ -> }

        arguments?.getParcelable<Data>(DATA)?.also { logItem ->
            alertDialog.setView(buildInfoView(logItem))
            alertDialog.setTitle(logItem.name)
        } ?: {
            alertDialog.setView(buildErrorView(R.string.LOG_ITEM_DETAILS_DIALOG_no_log_item_supplied))
            alertDialog.setTitle(R.string.LOG_ITEM_DETAILS_DIALOG_title)
        }

        return alertDialog.create()
    }

    private fun buildInfoView(data: Data): View {
        return requireActivity().layoutInflater
            .inflate(R.layout.dialog_log_item_details, null, false)
            .apply {
                findViewById<TextView>(R.id.syncLogItemDetailsId).text = data.taskId
//                findViewById<TextView>(R.id.syncLogItemDetailsMessage).text = logItem.operationName
                findViewById<TextView>(R.id.syncLogItemDetailsItemName).text = getString(R.string.LOG_ITEM_DETAILS_DIALOG_item_name_quotes, data.name)
                findViewById<TextView>(R.id.syncLogItemDetailsTime).text = CurrentDateTime.format(data.timestamp)

                val errorView = findViewById<TextView>(R.id.syncLogItemError)
                data.errorMsg?.also {
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

        const val DATA = "DATA"

        fun create(syncObjectLogItem: SyncObjectLogItem): LogItemDetailsDialog {
            return LogItemDetailsDialog().apply {
                arguments = bundleOf(
                    DATA to syncObjectLogItem
                )
            }
        }

        fun create(logOfSync: LogOfSync): LogItemDetailsDialog = LogItemDetailsDialog().apply {
            arguments = bundleOf(
                DATA to Data(
                    name = logOfSync.text,
                    timestamp = logOfSync.timestamp,
                    taskId = "item.id",
                    executionId = "executionId",
                    errorMsg = logOfSync.operationState.let {
                        if (it == OperationState.ERROR) it.name
                        else null
                    }
                )
            )
        }
    }

    @Parcelize
    data class Data(
        val name: String,
        val timestamp: Long,
        val taskId: String,
        val executionId: String,
        val errorMsg: String? = null
    ) : Parcelable
}