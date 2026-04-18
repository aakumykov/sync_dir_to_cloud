package com.github.aakumykov.sync_dir_to_cloud.app_settings

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.annotation.BoolRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.core.content.edit
import com.github.aakumykov.sync_dir_to_cloud.R
import javax.inject.Inject

class AppSettingsImpl @Inject constructor(
    private val resources: Resources,
    private val sharedPreferences: SharedPreferences,
) : AppSettings {

    private fun keyFromResources(@StringRes stringRes: Int): String = resources.getString(stringRes)
    private fun getBoolean(@BoolRes boolRes: Int): Boolean = resources.getBoolean(boolRes)
    private fun getInteger(@IntegerRes intRes: Int): Int = resources.getInteger(intRes)

    override var streamCopyingBufferSize: Int
        get() = sharedPreferences.getInt(
            keyFromResources(R.string.KEY_settings_stream_copying_buffer_size),
            getInteger(R.integer.DEFAULT_stream_copying_buffer_size)
        )
        set(value) {
            sharedPreferences.edit(commit = true) {
                putInt(
                    keyFromResources(R.string.KEY_settings_stream_copying_buffer_size),
                    value
                )
            }
        }


    override var fileParallelism: Int
        get() = sharedPreferences.getInt(
            keyFromResources(R.string.KEY_settings_file_transfer_parallelism),
            getInteger(R.integer.DEFAULT_settings_file_transfer_parallelism)
        )
        set(value) {
            return sharedPreferences.edit(commit = true) {
                putInt(
                    keyFromResources(R.string.KEY_settings_file_transfer_parallelism),
                    (if (value <= 0) getInteger(R.integer.DEFAULT_settings_file_transfer_parallelism)
                    else value)
                )
            }
        }

    override var restoreLostSourceAndTargetDirs: Boolean
        get() = sharedPreferences.getBoolean(
            keyFromResources(R.string.KEY_settings_re_create_missing_source_and_target_dirs),
            getBoolean(R.bool.DEFAULT_settings_re_create_missing_source_and_target_dirs)
        )
        set(value) {
            sharedPreferences.edit(commit = true) {
                putBoolean(
                    keyFromResources(R.string.KEY_settings_re_create_missing_source_and_target_dirs),
                    value
                )
            }
        }


    override var fileTransferRetardationMs: Int
        get() {
            return sharedPreferences.getInt(
                keyFromResources(R.string.KEY_settings_file_transfer_retardation_ms),
                getInteger(R.integer.DEFAULT_settings_file_transfer_retardation_ms)
            )
        }
        set(value) {
            sharedPreferences.edit(commit = true) {
                putInt(
                    keyFromResources(R.string.KEY_settings_file_transfer_retardation_ms),
                    (if (value < 0) getInteger(R.integer.DEFAULT_settings_file_transfer_retardation_ms)
                    else value)
                )
            }
        }

    override var backupIsCriticalOperation: Boolean
        get() {
            return sharedPreferences.getBoolean(
                keyFromResources(R.string.KEY_settings_backup_is_critical_operation),
                getBoolean(R.bool.DEFAULT_settings_backup_is_critical_operation)
            )
        }
        set(value) {
            sharedPreferences.edit(commit = true) {
                putBoolean(
                    keyFromResources(R.string.KEY_settings_backup_is_critical_operation),
                    value
                )
            }
        }


    override var dryRun: Boolean
        get() = sharedPreferences.getBoolean(
            keyFromResources(R.string.KEY_settings_dry_run),
            getBoolean(R.bool.DEFAULT_settings_dry_run)
        )
        set(value) {
            sharedPreferences.edit(commit = true) {
                putBoolean(
                    keyFromResources(R.string.KEY_settings_dry_run),
                    value
                )
            }
        }


    companion object {
        val TAG: String = AppSettingsImpl::class.java.simpleName
    }
}