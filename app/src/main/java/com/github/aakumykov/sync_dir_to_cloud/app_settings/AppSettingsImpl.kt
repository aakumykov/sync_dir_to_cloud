package com.github.aakumykov.sync_dir_to_cloud.app_settings

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.annotation.BoolRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import javax.inject.Inject
import androidx.core.content.edit
import com.github.aakumykov.sync_dir_to_cloud.config.DebugConfig

class AppSettingsImpl @Inject constructor(
    private val resources: Resources,
    private val sharedPreferences: SharedPreferences,
) : AppSettings {

    private fun keyFromResources(@StringRes stringRes: Int): String = resources.getString(stringRes)
    private fun getBoolean(@BoolRes boolRes: Int): Boolean = resources.getBoolean(boolRes)
    private fun getInteger(@IntegerRes intRes: Int): Int = resources.getInteger(intRes)


    override var restoreLostSourceAndTargetDirs: Boolean
        get() = sharedPreferences.getBoolean(
            keyFromResources(R.string.KEY_re_create_missing_source_and_target_dirs),
            getBoolean(R.bool.DEFAULT_re_create_missing_source_and_target_dirs)
        )
        set(value) { sharedPreferences.edit {
            putBoolean(
                keyFromResources(R.string.KEY_re_create_missing_source_and_target_dirs),
                value
            )
        } }


    private val DEFAULT_FILE_TRANSFER_RETARDATION_MS by lazy { getInteger(R.integer.DEFAULT_file_transfer_retardation_ms) }
    private val DEFAULT_BACKUP_IS_CRITICAL_OPERATION by lazy { getBoolean(R.bool.DEFAULT_backup_is_critical_operation) }

    override var fileTransferRetardationMs: Int
        get() {
            return sharedPreferences.getString(
                keyFromResources(R.string.KEY_file_transfer_retardation_ms),
                DEFAULT_FILE_TRANSFER_RETARDATION_MS.toString()
            )?.toInt() ?: DEFAULT_FILE_TRANSFER_RETARDATION_MS
        }
        set(value) { sharedPreferences.edit {
            putInt(
                keyFromResources(R.string.KEY_file_transfer_retardation_ms),
                value
            )
        } }

    override var backupIsCriticalOperation: Boolean
        get() {
            return sharedPreferences.getBoolean(
                keyFromResources(R.string.KEY_file_transfer_retardation_ms),
                DEFAULT_BACKUP_IS_CRITICAL_OPERATION
            )
        }
        set(value) { sharedPreferences.edit {
            putBoolean(
                keyFromResources(R.string.KEY_backup_is_critical_operation),
                value
            )
        } }
}