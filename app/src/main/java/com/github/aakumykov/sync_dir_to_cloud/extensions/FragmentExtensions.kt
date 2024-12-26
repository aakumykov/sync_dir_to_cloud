package com.github.aakumykov.sync_dir_to_cloud.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener

fun Fragment.listenForFragmentResult(requestKey: String, listener: FragmentResultListener) {
    childFragmentManager.setFragmentResultListener(requestKey, viewLifecycleOwner, listener)
}