package com.github.aakumykov.sync_dir_to_cloud.app_settings

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.annotation.BoolRes
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import javax.inject.Inject

class AppSettingsReaderImpl @Inject constructor(
    private val resources: Resources,
    private val sharedPreferences: SharedPreferences,
) : AppSettingsReader {

    private fun getString(@StringRes stringRes: Int): String = resources.getString(stringRes)
    private fun getBoolean(@BoolRes boolRes: Int): Boolean = resources.getBoolean(boolRes)

    override val isRestoreLostSourceAndTargetDirs: Boolean
        get() = sharedPreferences.getBoolean(
            getString(R.string.KEY_re_create_missing_source_and_target_dirs),
            getBoolean(R.bool.DEFAULT_re_create_missing_source_and_target_dirs)
        )
}