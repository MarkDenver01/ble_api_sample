package com.mpi.exam_sample.viewmodel.provider;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mpi.exam_sample.viewmodel.AccountViewModel;

public class AccountViewModelConstructor implements ViewModelProvider.Factory {
    private Context context;

    public AccountViewModelConstructor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AccountViewModel(context);
    }
}
