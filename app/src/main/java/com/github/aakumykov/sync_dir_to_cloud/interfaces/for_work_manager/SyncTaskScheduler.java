package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_work_manager;


import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask;

public interface SyncTaskScheduler {

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
