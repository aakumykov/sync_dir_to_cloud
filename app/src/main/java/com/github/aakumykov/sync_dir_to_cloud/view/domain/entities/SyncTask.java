package com.github.aakumykov.sync_dir_to_cloud.view.domain.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.gitlab.aakumykov.simple_list_view_driver.iTitleItem;

@Entity(tableName = "sync_tasks")
public class SyncTask implements iTitleItem {

    @PrimaryKey @NonNull public final String id;
    @ColumnInfo(name = "source_type") public final String sourceType;
    @ColumnInfo(name = "target_type") public final String targetType;
    @ColumnInfo(name = "source_path") public final String sourcePath;
    @ColumnInfo(name = "target_path") public final String targetPath;
    @ColumnInfo(name = "regularity") public final long regularity;

    @ColumnInfo(name = "is_progress") public boolean isProgress = false;
    @ColumnInfo(name = "is_success") public boolean isSuccess = false;
    @ColumnInfo(name = "progress_error") @Nullable public String progressError = null;

    @ColumnInfo(name = "is_scheduled") private boolean isScheduled = false;
    @ColumnInfo(name = "scheduling_error") @Nullable private String schedulingError = null;


    public SyncTask(
            @NonNull String id,
            String sourceType, String targetType,
            String sourcePath, String targetPath,
            long regularity
    ) {
        this.id = id;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.regularity = regularity;
    }

    public boolean isProgress() {
        return isProgress;
    }

    public void setIsProgress(boolean progress) {
        isProgress = progress;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean success) {
        isSuccess = success;
    }

    @Nullable
    public String getProgressError() {
        return progressError;
    }

    public void setProgressError(@Nullable String progressError) {
        this.progressError = progressError;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }


    public String getSchedulingError() {
        return schedulingError;
    }

    public void setSchedulingError(@Nullable String schedulingError) {
        this.schedulingError = schedulingError;
    }

    @Override
    public String getTitle() {
        return id;
    }
}
