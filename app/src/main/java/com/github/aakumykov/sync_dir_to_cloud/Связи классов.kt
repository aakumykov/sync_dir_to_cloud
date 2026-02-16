package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger
import com.github.aakumykov.sync_dir_to_cloud.utils.runInCoroutineExtended
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.base.BasicInstructionsProcessor
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.StreamToFileWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object.SyncObjectFileCopier
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.BackupInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.CollisionResolverInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.DeleteInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.DirCreationInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.FileCopyInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor_2.FileInstructionsProcessor
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.sync_task_processor.SyncTaskProcessor
import com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskWorker

/**
 * [SyncTaskWorker]
 * 🡫
 * [SyncTaskExecutor]
 * 🡫
 * [SyncTaskProcessor]
 * 🡫
 * [FileInstructionsProcessor]
 * 🡫
 * [FileCopyInstructionsProcessor] 🡨 [BasicInstructionsProcessor] 🡨 [runInCoroutineExtended] 🡨 [FileOperationLogger],
 * [DirCreationInstructionsProcessor] 🡨 [BasicInstructionsProcessor],
 * [CollisionResolverInstructionsProcessor],
 * [BackupInstructionsProcessor] 🡨 [BasicInstructionsProcessor],
 * [DeleteInstructionsProcessor] 🡨 [BasicInstructionsProcessor]
 * 🡫
 * [SyncObjectFileCopier]
 * 🡫
 * [StreamToFileWriter]
 * 🡫
 * [CloudWriter]
*/