package com.github.aakumykov.sync_dir_to_cloud.view.interfaces;

import androidx.lifecycle.LiveData;

import com.github.aakumykov.sync_dir_to_cloud.view.domain.entities.SyncTask;

import java.util.List;

public interface iSyncTaskManager {
    LiveData<List<SyncTask>> listSyncTasks();

    void createSyncTask(final SyncTask syncTask);
    SyncTask getSyncTask(final String id);
    void deleteSyncTask(final SyncTask syncTask);
}
