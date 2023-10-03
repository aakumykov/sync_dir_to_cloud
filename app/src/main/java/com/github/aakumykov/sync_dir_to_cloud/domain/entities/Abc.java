package com.github.aakumykov.sync_dir_to_cloud.domain.entities;

import java.util.UUID;

public class Abc extends AbcBase {

    public final String id;

    public Abc(AbcBase abcBase) {
        super(abcBase.sourcePath, abcBase.targetPath);
        id = UUID.randomUUID().toString();
    }

    public Abc(String id, String sourcePath, String targetPath) {
        super(sourcePath, targetPath);
        this.id = id;
    }
}
