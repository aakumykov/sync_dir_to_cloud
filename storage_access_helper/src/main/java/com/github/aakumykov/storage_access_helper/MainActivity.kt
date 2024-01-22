package com.github.aakumykov.kotlin_playground

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.github.aakumykov.kotlin_playground.databinding.ActivityMainBinding
import com.github.aakumykov.kotlin_playground.extensions.showToast
import com.github.aakumykov.storage_access_helper.StorageAccessHelper
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storageAccessHelper: StorageAccessHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.openStorageAccessSettings.setOnClickListener { storageAccessHelper.openStorageAccessSettings() }

        binding.listFilesButton.setOnClickListener {
            storageAccessHelper.requestStorageAccess { isGranted ->
                if (isGranted) listFilesReal()
                else showToast("Нет доступа к памяти")
            }
        }

        storageAccessHelper = StorageAccessHelper.create(this)
    }

    override fun onResume() {
        super.onResume()
        displayHasStorageAccess()
    }

    private fun displayHasStorageAccess() {
        binding.hasStorageAccessView.text =
            if (storageAccessHelper.hasStorageAccess()) "Есть доступ к памяти"
            else "Нет доступа к памяти"
    }


    private fun listFilesReal() {

        val dir = File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS)

        val list = dir.list()

        val dirPathForMessage = "'${dir.absolutePath}'"

        binding.messageView.text = when {
            (null == list) -> "не удалось прочитать каталог $dirPathForMessage"
            list.isEmpty() -> "в каталоге $dirPathForMessage нет файлов"
            else -> list.toList().reduce { a, b -> a + "\n" + b }
        }
    }
}