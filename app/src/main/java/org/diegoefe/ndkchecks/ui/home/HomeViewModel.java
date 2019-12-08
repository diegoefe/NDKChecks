package org.diegoefe.ndkchecks.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mHello;

    public HomeViewModel() {
        mHello = new MutableLiveData<>();
    }

    LiveData<String> getText() {
        return mHello;
    }
}