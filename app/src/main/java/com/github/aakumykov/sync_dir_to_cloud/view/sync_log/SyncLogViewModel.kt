package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileOperationJobIdReader
import com.github.aakumykov.sync_dir_to_cloud.job_holdes.OperationJobsHolder
import com.github.aakumykov.sync_dir_to_cloud.repository.LogOfSyncRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncTaskRepository
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SyncLogViewModel(
    private val logOfSyncRepository: LogOfSyncRepository,
    // TODO: заменить на SyncTaskStateReader
    private val syncTaskRepository: SyncTaskRepository,
    private val fileOperationJobIdReader: FileOperationJobIdReader,
) : ViewModel() {

    private var isFirstRun = true

    private val _logOfSyncListFlow: MutableStateFlow<List<LogOfSync>> = MutableStateFlow(emptyList())
    val logOfSyncListFlow: Flow<List<LogOfSync>> = _logOfSyncListFlow

    private val _isRunningFlow = MutableStateFlow(false)
    val isRunningFlow: Flow<Boolean> = _isRunningFlow

    suspend fun startWorking(taskId: String, executionId: String) {
        if (isFirstRun) {
            isFirstRun = false

            viewModelScope.launch {
                logOfSyncRepository.listAsFlow(taskId, executionId).collect {
                    _logOfSyncListFlow.emit(it)
                }
            }

            viewModelScope.launch {
                syncTaskRepository.getTaskStateFlow(taskId).collect { executionState ->
                    _isRunningFlow.emit(ExecutionState.RUNNING == executionState)
                }
            }
        }
    }


    fun cancelJob(logItemId: String) {
        Log.d(TAG, "cancelJob() called with: logItemId = $logItemId")

        viewModelScope.launch(NonCancellable) {

            fileOperationJobIdReader.getJobId(logItemId)?.also { jobId ->
                Log.d(TAG, "jobId: $jobId")

                OperationJobsHolder.getJob(jobId)?.also { job ->
                    Log.d(TAG, job.toString())
                    job.cancel()
                } ?: run {
                    Log.d(TAG, "Задача с jobId=${jobId} не найдена.")
                }

            } ?: run {
                Log.d(TAG, "jobId не найден")
            }

        }
    }

    companion object {
        val TAG: String = SyncLogViewModel::class.java.simpleName
    }
}
