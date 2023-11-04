package com.github.aakumykov.file_selector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.aakumykov.cloud_file_lister.file_lister.FSItem

class FileSelectionViewModel : ViewModel() {

    private val _currentPathMutableLiveData: MutableLiveData<String> = MutableLiveData()
    val currentPath get(): LiveData<String> = _currentPathMutableLiveData

    private val _fileListMutableLiveData: MutableLiveData<List<FSItem>> = MutableLiveData()
    val fileList get(): LiveData<List<FSItem>> = _fileListMutableLiveData

    private val _errorMutableLiveData: MutableLiveData<Throwable> = MutableLiveData()
    val errorMessage get(): LiveData<Throwable> = _errorMutableLiveData

    private val _selectedList: MutableList<FSItem> = mutableListOf()
    private val _selectedListMutableLiveData: MutableLiveData<List<FSItem>> = MutableLiveData(_selectedList)
    val selectedList get(): LiveData<List<FSItem>> = _selectedListMutableLiveData


    fun setFileList(list: List<FSItem>) {
        _fileListMutableLiveData.value = list
    }


    fun toggleInSelectionList(fsItem: FSItem) {
        if (_selectedList.contains(fsItem))
            _selectedList.remove(fsItem)
        else
            _selectedList.add(fsItem)
        _selectedListMutableLiveData.value = _selectedList
    }


    fun clearSelectionList() {
        _selectedList.clear()
    }


    fun setCurrentPath(path: String) {
        _currentPathMutableLiveData.value = path
    }

    fun setError(throwable: Throwable) {
        _errorMutableLiveData.value = throwable
    }

    fun getSelectedList(): List<FSItem> {
        return _selectedList
    }
}