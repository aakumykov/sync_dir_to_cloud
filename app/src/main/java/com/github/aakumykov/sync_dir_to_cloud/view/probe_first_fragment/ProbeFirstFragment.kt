package com.github.aakumykov.sync_dir_to_cloud.view.probe_first_fragment

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.view.MenuStateViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.CustomMenuItem
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuState

class ProbeFirstFragment : Fragment(R.layout.fragment_probe_first) {

    private val menuItems = arrayOf(
        CustomMenuItem(
            id = R.id.actionStartStopTask,
            title = R.string.MENU_ITEM_action_start_stop_task,
            icon = R.drawable.ic_task_start_toolbar,
            action = { Toast.makeText(requireContext(),TAG,Toast.LENGTH_SHORT).show() })
    )

    private val menuStateViewModel: MenuStateViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        menuStateViewModel.sendMenuState(MenuState(*menuItems))
    }

    companion object {
        fun create(): ProbeFirstFragment {
            return ProbeFirstFragment()
        }

        val TAG: String = ProbeFirstFragment::class.java.simpleName
    }
}