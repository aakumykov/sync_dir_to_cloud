package com.github.aakumykov.sync_dir_to_cloud.repository.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth;
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask;
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTaskBase;

import java.util.List;

@Dao
public abstract class SyncTaskDAO { // TODO: переименовать в SyncTaskBaseDAO ?

    @Insert
    abstract void add(SyncTaskBase syncTaskBase);

    @Delete
    abstract void delete(SyncTaskBase syncTaskBase);

    @Update
    abstract void update(SyncTaskBase syncTaskBase);


    // FIXME: Не SyncTaskBase, а SyncTask, вынести в отдельный DAO...

    @Query("SELECT * FROM sync_tasks")
    public abstract LiveData<List<SyncTask>> list();

    @Query("SELECT * FROM sync_tasks WHERE id = :id")
    public abstract SyncTask get(String id);


    @Transaction
    public void add(SyncTask syncTask) {
        add(syncTask.getTask());
        add(syncTask.getCloudAuth());
    }

    @Insert
    abstract void add(CloudAuth cloudAuth);

    @Transaction
    public void delete(SyncTask syncTask) {
        delete(syncTask.getTask());
        delete(syncTask.getCloudAuth());
    }

    @Delete
    abstract void delete(CloudAuth cloudAuth);

    @Transaction
    public void update(SyncTask syncTask) {
        update(syncTask.getTask());
        update(syncTask.getCloudAuth());
    }

    @Update
    abstract void update(CloudAuth cloudAuth);
}
