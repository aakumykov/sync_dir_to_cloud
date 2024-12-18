package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.SyncLogViewHolderClickCallbacks

class LogOfSyncAdapterRV(private val cancellationCallback: SyncLogViewHolderClickCallbacks)
    : ListAdapter<LogOfSync, LogOfSyncViewHolderRV>(LogOfSyncDiffer())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogOfSyncViewHolderRV {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sync_log_view_holder, parent, false)
        return LogOfSyncViewHolderRV(view, cancellationCallback)
    }


    override fun onBindViewHolder(viewHolder: LogOfSyncViewHolderRV, position: Int) {
        viewHolder.fill(getItem(position))
    }

    override fun submitList(list: List<LogOfSync>?) {
        super.submitList(list)
        /*list?.size.also { size ->
            if (0 == size)
                notifyItemRangeRemoved(0, itemCount)
        }*/
    }

    class LogOfSyncDiffer : DiffUtil.ItemCallback<LogOfSync>() {

        override fun areItemsTheSame(oldItem: LogOfSync, newItem: LogOfSync): Boolean {
            return oldItem.operationId == newItem.operationId &&
                    oldItem.text == newItem.text &&
                    oldItem.subText == newItem.subText
        }

        override fun areContentsTheSame(oldItem: LogOfSync, newItem: LogOfSync): Boolean {
//            return oldItem.isEqualsWith(newItem)
            return oldItem.text == newItem.text &&
                    oldItem.subText == newItem.subText &&
                    oldItem.timestamp == newItem.timestamp &&
                    oldItem.operationState == newItem.operationState &&
                    oldItem.errorMessage == newItem.errorMessage &&
                    oldItem.progress == newItem.progress &&
                    oldItem.isCancelable == newItem.isCancelable &&
                    oldItem.operationId == newItem.operationId
        }

        /*override fun getChangePayload(oldItem: LogOfSync, newItem: LogOfSync): Any? {
            return super.getChangePayload(oldItem, newItem)
        }*/
    }
}