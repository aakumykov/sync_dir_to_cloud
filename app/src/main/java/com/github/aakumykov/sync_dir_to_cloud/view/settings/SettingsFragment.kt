package com.github.aakumykov.sync_dir_to_cloud.view.settings

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.activityViewModels
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.view.MenuStateViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.CustomMenuItem
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuState
import kotlin.getValue

class SettingsFragment : PreferenceFragmentCompat() {

    private val menuItems = emptyArray<CustomMenuItem>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        pageTitleViewModel.setPageTitle(getString(R.string.FRAGMENT_SETTINGS_title))
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<EditTextPreference>(getString(R.string.KEY_file_transfer_retardation_ms))
            ?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
    }


    override fun onResume() {
        super.onResume()
        menuStateViewModel.sendMenuState(MenuState(*menuItems))
    }


    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()
    private val menuStateViewModel: MenuStateViewModel by activityViewModels()


    companion object {
        fun create(): SettingsFragment = SettingsFragment()
    }
}