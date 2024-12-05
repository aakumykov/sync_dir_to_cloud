package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

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
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime
import kotlinx.parcelize.Parcelize

class LogItemDetailsDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val alertDialog = AlertDialog.Builder(requireContext())
            .setNegativeButton(R.string.LOG_ITEM_DETAILS_DIALOG_close_button) { _,_ -> }

        arguments?.getParcelable<SyncLogDialogInfo>(SYNC_LOG_DIALOG_INFO)?.also { dialogInfo ->
            alertDialog.setView(buildInfoView(dialogInfo))
            alertDialog.setTitle(dialogInfo.title)
        } ?: {
            alertDialog.setView(buildErrorView(R.string.LOG_ITEM_DETAILS_DIALOG_no_log_item_supplied))
            alertDialog.setTitle(R.string.LOG_ITEM_DETAILS_DIALOG_title)
        }

        return alertDialog.create()
    }

    private fun buildInfoView(dialogInfo: SyncLogDialogInfo): View {
        return requireActivity().layoutInflater
            .inflate(R.layout.dialog_log_item_details, null, false)
            .apply {
//                findViewById<TextView>(R.id.syncLogItemDetailsId).text = dialogInfo.itemId
//                findViewById<TextView>(R.id.syncLogItemDetailsMessage).text = logItem.operationName
                findViewById<TextView>(R.id.syncLogItemDetailsItemName).text = getString(R.string.LOG_ITEM_DETAILS_DIALOG_item_name_quotes, dialogInfo.title)
                findViewById<TextView>(R.id.syncLogItemDetailsTime).text = CurrentDateTime.format(dialogInfo.timestamp)

                val errorView = findViewById<TextView>(R.id.syncLogItemError)
                dialogInfo.errorMessage?.also {
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

        const val SYNC_LOG_DIALOG_INFO = "SYNC_LOG_DIALOG_INFO"

        fun create(dialogInfo: SyncLogDialogInfo): LogItemDetailsDialog {
            return LogItemDetailsDialog().apply {
                arguments = bundleOf(
                    SYNC_LOG_DIALOG_INFO to dialogInfo
                )
            }
        }
    }
}

@Parcelize
data class SyncLogDialogInfo(
//    val itemId: String,
    val title: String,
    val subtitle: String,
    val errorMessage: String?,
    val operationState: OperationState,
    val timestamp: Long,
): Parcelable
