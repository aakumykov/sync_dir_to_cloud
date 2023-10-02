package com.github.aakumykov.sync_dir_to_cloud.view.domain.use_cases.sync_task;

import android.util.Log;

import com.github.aakumykov.sync_dir_to_cloud.view.domain.entities.SyncTask;
import com.github.aakumykov.sync_dir_to_cloud.view.interfaces.iSyncTaskStarter;
import com.github.aakumykov.sync_dir_to_cloud.view.interfaces.iSyncTaskStopper;
import com.github.aakumykov.sync_dir_to_cloud.view.interfaces.iSyncTaskUpdater;
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils;

public class StartStopSyncTaskUseCase {

    private static final String TAG = StartStopSyncTaskUseCase.class.getSimpleName();
    private final iSyncTaskStarter mSyncTaskStarter;
    private final iSyncTaskStopper mSyncTaskStopper;
    private final iSyncTaskUpdater mSyncTaskUpdater;

    public StartStopSyncTaskUseCase(iSyncTaskStarter syncTaskStarter, iSyncTaskStopper syncTaskStopper, iSyncTaskUpdater iSyncTaskUpdater) {
        mSyncTaskStarter = syncTaskStarter;
        mSyncTaskStopper = syncTaskStopper;
        mSyncTaskUpdater = iSyncTaskUpdater;
    }


    public void startSyncTask(final SyncTask syncTask) {
        mSyncTaskStarter.startSyncTask(syncTask, new iSyncTaskStarter.Callbacks() {
            @Override
            public void onSyncTaskStarted() {
                syncTask.setIsProgress(true);
                syncTask.setProgressError(null);
                mSyncTaskUpdater.updateSyncTask(syncTask);
            }

            @Override
            public void onSyncTaskStartingError(Exception e) {
                syncTask.setIsProgress(false);
                syncTask.setIsSuccess(false);
                syncTask.setProgressError(ExceptionUtils.getErrorMessage(e));
                mSyncTaskUpdater.updateSyncTask(syncTask);
                Log.e(TAG, ExceptionUtils.getErrorMessage(e), e);
            }
        });
    }


    public void stopSyncTask(final SyncTask syncTask) {
        mSyncTaskStopper.stopSyncTask(syncTask, new iSyncTaskStopper.Callbacks() {
            @Override
            public void onSyncTaskStopped() {
                syncTask.setIsProgress(false);
                syncTask.setIsSuccess(false);
                syncTask.setProgressError(null);
                mSyncTaskUpdater.updateSyncTask(syncTask);
            }

            @Override
            public void onSyncTaskStoppingError(Exception e) {
                syncTask.setIsProgress(false);
                syncTask.setIsSuccess(false);
                syncTask.setProgressError(ExceptionUtils.getErrorMessage(e));
                mSyncTaskUpdater.updateSyncTask(syncTask);
                Log.e(TAG, ExceptionUtils.getErrorMessage(e), e);
            }
        });
    }
}
