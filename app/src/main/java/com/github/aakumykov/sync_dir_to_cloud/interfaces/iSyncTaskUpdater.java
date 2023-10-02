package com.github.aakumykov.sync_dir_to_cloud.interfaces;

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask;

public interface iSyncTaskUpdater {
    void updateSyncTask(final SyncTask syncTask);
}
