package com.github.aakumykov.sync_dir_to_cloud.repository.sync_task;

import androidx.lifecycle.LiveData;

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask;
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskManager;
import com.github.aakumykov.sync_dir_to_cloud.interfaces.iSyncTaskUpdater;
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_task.data_sources.SyncTaskLocalDataSource;

import java.util.List;

public class SyncTaskRepository implements iSyncTaskManager, iSyncTaskUpdater {

    private final SyncTaskLocalDataSource mSyncTaskLocalDataSource;


    public SyncTaskRepository(SyncTaskLocalDataSource syncTaskLocalDataSource) {
        mSyncTaskLocalDataSource = syncTaskLocalDataSource;
    }


    @Override
    public LiveData<List<SyncTask>> listSyncTasks() {
        return mSyncTaskLocalDataSource.listSyncTasks();
    }


    @Override
    public SyncTask getSyncTask(String id) {
        return null;
    }


    @Override
    public void createSyncTask(SyncTask syncTask) {
        mSyncTaskLocalDataSource.addTask(syncTask);
    }


    @Override
    public void deleteSyncTask(SyncTask syncTask) {

    }

    @Override
    public void updateSyncTask(SyncTask syncTask) {

    }
}
