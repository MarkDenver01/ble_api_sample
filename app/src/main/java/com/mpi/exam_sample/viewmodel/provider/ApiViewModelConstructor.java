package com.mpi.exam_sample.viewmodel.provider;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mpi.exam_sample.viewmodel.ApiViewModel;

public class ApiViewModelConstructor implements ViewModelProvider.Factory {
    private Context context;

    public ApiViewModelConstructor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ApiViewModel(context);
    }
}
