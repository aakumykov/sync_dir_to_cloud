package com.github.aakumykov.sync_dir_to_cloud.repository.sync_task.data_sources;

import androidx.lifecycle.LiveData;

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask;
import com.github.aakumykov.sync_dir_to_cloud.room.SyncTaskDAO;

import java.util.List;

public class SyncTaskLocalDataSource {

    private final SyncTaskDAO mSyncTaskDAO;

    public SyncTaskLocalDataSource(SyncTaskDAO syncTaskDAO) {
        mSyncTaskDAO = syncTaskDAO;
    }

    public LiveData<List<SyncTask>> listSyncTasks() {
        return mSyncTaskDAO.list();
    }

    public void addTask(SyncTask syncTask) {
        mSyncTaskDAO.add(syncTask);
    }
}
