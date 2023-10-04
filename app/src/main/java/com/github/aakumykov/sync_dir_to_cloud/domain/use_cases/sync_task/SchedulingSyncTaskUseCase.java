package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task;

import android.util.Log;

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask;
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskScheduler;
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskUpdater;
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils;

public class SchedulingSyncTaskUseCase {

    private static final String TAG = SchedulingSyncTaskUseCase.class.getSimpleName();
    private final iSyncTaskScheduler mSyncTaskScheduler;
    private final iSyncTaskUpdater mSyncTaskUpdater;

    public SchedulingSyncTaskUseCase(iSyncTaskScheduler syncTaskScheduler, iSyncTaskUpdater syncTaskUpdater) {
        mSyncTaskScheduler = syncTaskScheduler;
        mSyncTaskUpdater = syncTaskUpdater;
    }

    public void scheduleSyncTask(final SyncTask syncTask) {
        mSyncTaskScheduler.scheduleSyncTask(syncTask, new iSyncTaskScheduler.ScheduleCallbacks() {
            @Override
            public void onSyncTaskScheduleSuccess() {
//                syncTask.setScheduled(true);
//                syncTask.setSchedulingError(null);
                mSyncTaskUpdater.updateSyncTask(syncTask);
            }

            @Override
            public void onSyncTaskScheduleError(Exception e) {
//                syncTask.setScheduled(false);
//                syncTask.setSchedulingError(ExceptionUtils.getErrorMessage(e));
                mSyncTaskUpdater.updateSyncTask(syncTask);
                Log.e(TAG, ExceptionUtils.getErrorMessage(e), e);
            }
        });
    }


    public void unScheduleSyncTask(final SyncTask syncTask) {
        mSyncTaskScheduler.unScheduleSyncTask(syncTask, new iSyncTaskScheduler.UnScheduleCallbacks() {
            @Override
            public void onSyncTaskUnScheduleSuccess() {
//                syncTask.setScheduled(false);
//                syncTask.setSchedulingError(null);
                mSyncTaskUpdater.updateSyncTask(syncTask);
            }

            @Override
            public void onSyncTaskUnScheduleError(Exception e) {
//                syncTask.setScheduled(true);
//                syncTask.setSchedulingError(ExceptionUtils.getErrorMessage(e));
                mSyncTaskUpdater.updateSyncTask(syncTask);
                Log.e(TAG, ExceptionUtils.getErrorMessage(e), e);
            }
        });
    }
}
