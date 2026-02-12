package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentSyncLogComposeBinding
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.SyncLogViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose.ui.theme.Sync_dir_to_cloudTheme

class SyncLogFragmentCompose : Fragment(R.layout.fragment_sync_log_compose) {

    private lateinit var syncLogViewModel: SyncLogViewModel
    private val taskId: String? get() = arguments?.getString(GlobalConstants.TASK_ID)
    private val executionId: String? get() = arguments?.getString(GlobalConstants.EXECUTION_ID)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FragmentSyncLogComposeBinding.bind(view).root
            .findViewById<ComposeView>(R.id.syncLogComposeView)
            .apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    Sync_dir_to_cloudTheme {
                        if (null != taskId && null != executionId) {
                            SyncLogItem(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = syncLogViewModel,
                                taskId = taskId!!,
                                executionId = executionId!!,
                            )
                        } else {
                            Text(
                                text = resources.getString(R.string.SYNC_LOG_error_insufficient_arguments),
//                                color = Color(resources.getColor(R.color.error, null)),
//                                textAlignment = TextAlign.Center,
//                                modifier = Modifier.fillMaxWidth().padding(12.dp)
                            )
                        }
                    }
                }
            }

        syncLogViewModel = DaggerViewModelHelper.get(this, SyncLogViewModel::class.java)
    }

    companion object {
        fun create(taskId: String, executionId: String): SyncLogFragmentCompose {
            return SyncLogFragmentCompose().apply {
                arguments = bundleOf(
                    GlobalConstants.TASK_ID to taskId,
                    GlobalConstants.EXECUTION_ID to executionId,
                )
            }
        }
    }
}