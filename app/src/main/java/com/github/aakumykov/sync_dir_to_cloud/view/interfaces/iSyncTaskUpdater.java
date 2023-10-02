package com.github.aakumykov.sync_dir_to_cloud.view.interfaces;

import com.github.aakumykov.sync_dir_to_cloud.view.domain.entities.SyncTask;

public interface iSyncTaskUpdater {
    void updateSyncTask(final SyncTask syncTask);
}
