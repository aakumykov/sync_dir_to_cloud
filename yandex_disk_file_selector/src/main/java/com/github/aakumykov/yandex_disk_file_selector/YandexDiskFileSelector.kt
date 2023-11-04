package com.github.aakumykov.yandex_disk_file_selector

import android.os.Bundle
import android.view.View
import com.github.aakumykov.cloud_file_lister.file_lister.FileLister
import com.github.aakumykov.cloud_file_lister.yandex_file_lister.YandexFileLister
import com.github.aakumykov.file_selector.FileSelectionDialog

class YandexDiskFileSelector : FileSelectionDialog() {

    private var yandexAuthToken: String? = null

    private val yandexFileLister: YandexFileLister by lazy {
        YandexFileLister(yandexAuthToken!!)
    }


    override fun fileLister(): FileLister {
        return yandexFileLister
    }


    // FIXME: случай с некорректным токеном
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yandexAuthToken = arguments?.getString(AUTH_TOKEN)
    }


    companion object {
        fun create(yandexAuthToken: String) : YandexDiskFileSelector {
            return YandexDiskFileSelector().also {
                it.arguments = Bundle().apply {
                    putString(AUTH_TOKEN, yandexAuthToken)
                }
            }
        }
    }
}