package com.github.aakumykov.sync_dir_to_cloud.interfaces;

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask;

public interface iSyncTaskStopper {

    void stopSyncTask(final SyncTask syncTask, final Callbacks callbacks);

    interface Callbacks {
        void onSyncTaskStopped();
        void onSyncTaskStoppingError(Exception e);
    }
}
