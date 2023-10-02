package com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task;

import androidx.lifecycle.LiveData;

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask;
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskManager;
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskUpdater;

import java.util.List;

public class SyncTaskManagingUseCase {

    private final iSyncTaskManager mSyncTaskManager;
    private final iSyncTaskUpdater mISyncTaskUpdater;

    public SyncTaskManagingUseCase(iSyncTaskManager syncTaskManager,
                                   iSyncTaskUpdater syncTaskUpdater) {
        mSyncTaskManager = syncTaskManager;
        mISyncTaskUpdater = syncTaskUpdater;
    }


    public LiveData<List<SyncTask>> listSyncTasks() {
        return mSyncTaskManager.listSyncTasks();
    }


    public void addSyncTask(final SyncTask syncTask) {
        mSyncTaskManager.createSyncTask(syncTask);
    }


    public void updateSyncTask(final SyncTask syncTask) {
        mISyncTaskUpdater.updateSyncTask(syncTask);
    }


    public SyncTask getSyncTask(final String id) {
        return mSyncTaskManager.getSyncTask(id);
    }


    public void deleteSyncTask(final SyncTask syncTask) {
        mSyncTaskManager.deleteSyncTask(syncTask);
    }
}
