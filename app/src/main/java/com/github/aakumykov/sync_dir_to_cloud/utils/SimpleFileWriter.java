package com.github.aakumykov.sync_dir_to_cloud.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SimpleFileWriter {

    private final static String NEW_LINE = "\n";
    private final File mTargetFile;

    public SimpleFileWriter(final File targetDir, final String fileNameInDir) {
        this(new File(targetDir, fileNameInDir));
    }

    public SimpleFileWriter(final File targetFile) {
        mTargetFile = targetFile;
    }

    public void write(final String text) throws IOException {
        final OutputStream outputStream = new FileOutputStream(mTargetFile, true);
        outputStream.write(text.getBytes());
        outputStream.close();
    }

    public void writeln(final String text) throws IOException {
        write(text+NEW_LINE);
    }

    public void lnWrite(final String text) throws IOException {
        write(NEW_LINE + text);
    }
}
