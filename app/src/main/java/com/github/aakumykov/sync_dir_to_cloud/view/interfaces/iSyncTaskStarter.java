package com.github.aakumykov.sync_dir_to_cloud.view.interfaces;

import com.github.aakumykov.sync_dir_to_cloud.view.domain.entities.SyncTask;

public interface iSyncTaskStarter {

    void startSyncTask(final SyncTask syncTask, Callbacks callbacks);

    interface Callbacks {
        void onSyncTaskStarted();
        void onSyncTaskStartingError(Exception e);
    }
}
