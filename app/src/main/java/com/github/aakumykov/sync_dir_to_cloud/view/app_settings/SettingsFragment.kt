package com.github.aakumykov.sync_dir_to_cloud.view.app_settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.github.aakumykov.sync_dir_to_cloud.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}