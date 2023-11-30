package com.github.aakumykov.sync_dir_to_cloud.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils

object Logger {

    private val messageList: MutableList<String> = mutableListOf()
    private val _messageListMutableLiveData: MutableLiveData<List<String>> = MutableLiveData()

    val messages get(): LiveData<List<String>> = _messageListMutableLiveData

    fun d(tag: String, message: String) {
        Log.d(tag, message)
        messageList.add("$tag: $message")
        postNewList()
    }

    fun e(tag: String, e: Exception) {
        val message = ExceptionUtils.getErrorMessage(e)
        Log.e(tag, message)
        messageList.add("$tag: ОШИБКА: $message")
        postNewList()
    }

    fun clear() {
        messageList.clear()
        postNewList()
    }


    private fun postNewList() {
        _messageListMutableLiveData.postValue(messageList)
    }
}