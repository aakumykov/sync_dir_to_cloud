package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamToFileWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectFileCopier
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor.BackupInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor.CollisionResolverInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor.DeleteInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor.DirCreationInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor.FileCopyingInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor.CommonFileInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger.DatabaseFileOperationLogger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.sync_task_processor.SyncTaskProcessor
import com.github.aakumykov.sync_dir_to_cloud.utils.runInCoroutineExtended
import com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskWorker

/**
 * [SyncTaskWorker]
 * 🡫
 * [SyncTaskExecutor]
 * 🡫
 * [SyncTaskProcessor]
 * 🡫
 * [CommonFileInstructionsProcessor]
 * 🡫
 * [BasicFileInstructionsProcessor (абстрактный)]
 * 🡫
 * [FileCopyingInstructionsProcessor] 🡨 [runInCoroutineExtended] 🡨 [DatabaseFileOperationLogger],
 * [DirCreationInstructionsProcessor],
 * [CollisionResolverInstructionsProcessor],
 * [BackupInstructionsProcessor],
 * [DeleteInstructionsProcessor]
 * 🡫
 * [SyncObjectFileCopier] (сюда приходит прогресс)
 * 🡫
 * [StreamToFileWriter]
 * 🡫
 * [CloudWriter]
*/