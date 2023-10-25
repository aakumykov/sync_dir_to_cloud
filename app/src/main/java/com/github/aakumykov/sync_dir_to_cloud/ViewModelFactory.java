package com.github.aakumykov.sync_dir_to_cloud;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> mViewModelProvidersMap;

    @Inject
    public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModelProvidersMap) {
        mViewModelProvidersMap = viewModelProvidersMap;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (mViewModelProvidersMap.containsKey(modelClass))
            return (T) mViewModelProvidersMap.get(modelClass).get();
        else
            throw new IllegalArgumentException("Неизвестный класс ViewModel: "+modelClass);
    }
}