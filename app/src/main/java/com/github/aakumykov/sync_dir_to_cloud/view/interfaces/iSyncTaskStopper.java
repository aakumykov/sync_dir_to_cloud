package com.github.aakumykov.sync_dir_to_cloud.view.interfaces;

import com.github.aakumykov.sync_dir_to_cloud.view.domain.entities.SyncTask;

public interface iSyncTaskStopper {

    void stopSyncTask(final SyncTask syncTask, final Callbacks callbacks);

    interface Callbacks {
        void onSyncTaskStopped();
        void onSyncTaskStoppingError(Exception e);
    }
}
