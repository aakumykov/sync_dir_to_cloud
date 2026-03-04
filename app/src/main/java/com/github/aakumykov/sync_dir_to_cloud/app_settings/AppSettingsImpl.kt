package com.github.aakumykov.sync_dir_to_cloud.app_settings

import android.content.SharedPreferences
import android.content.res.Resources
import android.util.Log
import androidx.annotation.BoolRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.compose.ui.unit.IntRect
import com.github.aakumykov.sync_dir_to_cloud.R
import javax.inject.Inject
import androidx.core.content.edit
import com.github.aakumykov.sync_dir_to_cloud.config.DEFAULT_BACKUP_IS_CRITICAL_OPERATION
import com.github.aakumykov.sync_dir_to_cloud.config.DEFAULT_FILE_TRANSFER_PARALLELISM
import com.github.aakumykov.sync_dir_to_cloud.config.DEFAULT_FILE_TRANSFER_RETARDATION_MS
import com.github.aakumykov.sync_dir_to_cloud.config.SETTINGS_KEY_BACKUP_IS_CRITICAL_OPERATION
import com.github.aakumykov.sync_dir_to_cloud.config.SETTINGS_KEY_FILE_TRANSFER_PARALLELISM
import com.github.aakumykov.sync_dir_to_cloud.config.SETTINGS_KEY_FILE_TRANSFER_RETARDATION_MS

class AppSettingsImpl @Inject constructor(
    private val resources: Resources,
    private val sharedPreferences: SharedPreferences,
) : AppSettings {

    private fun keyFromResources(@StringRes stringRes: Int): String = resources.getString(stringRes)
    private fun getBoolean(@BoolRes boolRes: Int): Boolean = resources.getBoolean(boolRes)
    private fun getInteger(@IntegerRes intRes: Int): Int = resources.getInteger(intRes)

    override var fileParallelism: Int
        get() = sharedPreferences.getString(
            SETTINGS_KEY_FILE_TRANSFER_PARALLELISM,
            DEFAULT_FILE_TRANSFER_PARALLELISM
        ).let { Integer.parseInt(it!!) }
        set(value) {
            return sharedPreferences.edit {
                putString(
                    SETTINGS_KEY_FILE_TRANSFER_PARALLELISM,
                    (if (value <= 0) DEFAULT_FILE_TRANSFER_PARALLELISM
                    else value).toString()
                )
            }
        }

    override var restoreLostSourceAndTargetDirs: Boolean
        get() = sharedPreferences.getBoolean(
            keyFromResources(R.string.KEY_settings_re_create_missing_source_and_target_dirs),
            getBoolean(R.bool.DEFAULT_settings_re_create_missing_source_and_target_dirs)
        )
        set(value) {
            sharedPreferences.edit {
                putBoolean(keyFromResources(R.string.KEY_settings_re_create_missing_source_and_target_dirs), value)
            }
        }


    override var fileTransferRetardationMs: Int
        get() {
            return sharedPreferences.getString(
                SETTINGS_KEY_FILE_TRANSFER_RETARDATION_MS,
                DEFAULT_FILE_TRANSFER_RETARDATION_MS
            ).let {
                Integer.parseInt(it!!)
            }
        }
        set(value) {
            sharedPreferences.edit {
                putString(
                    SETTINGS_KEY_FILE_TRANSFER_RETARDATION_MS,
                    (if (value < 0) DEFAULT_FILE_TRANSFER_RETARDATION_MS
                    else value).toString()
                )
            }
        }

    override var backupIsCriticalOperation: Boolean
        get() {
            return sharedPreferences.getBoolean(
                SETTINGS_KEY_BACKUP_IS_CRITICAL_OPERATION,
                DEFAULT_BACKUP_IS_CRITICAL_OPERATION
            )
        }
        set(value) {
            sharedPreferences.edit {
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
            sharedPreferences.edit {
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