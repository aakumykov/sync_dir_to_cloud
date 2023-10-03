package com.github.aakumykov.sync_dir_to_cloud.domain.entities;

public class AbcBase {

    public final String sourcePath;
    public final String targetPath;

    public AbcBase(String sourcePath, String targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }
}
