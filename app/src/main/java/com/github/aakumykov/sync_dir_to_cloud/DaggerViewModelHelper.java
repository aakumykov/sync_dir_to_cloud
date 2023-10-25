package com.github.aakumykov.sync_dir_to_cloud;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class DaggerViewModelHelper {

    public static <T extends ViewModel> T get(
            ViewModelStoreOwner viewModelStoreOwner,
            Class<T> viewModelClass
    ) {
        return new ViewModelProvider(
                viewModelStoreOwner,
                App.Companion.getAppComponent().getViewModelFactory()
        ).get(viewModelClass);
    }
}
