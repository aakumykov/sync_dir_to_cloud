package com.github.aakumykov.sync_dir_to_cloud.view.interfaces;

import com.github.aakumykov.sync_dir_to_cloud.view.domain.entities.SyncTask;

public interface iSyncTaskScheduler {

    void scheduleSyncTask(final SyncTask syncTask, ScheduleCallbacks callbacks);
    void unScheduleSyncTask(final SyncTask syncTask, UnScheduleCallbacks callbacks);

    interface ScheduleCallbacks {
        void onSyncTaskScheduleSuccess();
        void onSyncTaskScheduleError(Exception e);
    }

    interface UnScheduleCallbacks {
        void onSyncTaskUnScheduleSuccess();
        void onSyncTaskUnScheduleError(Exception e);
    }
}
