package ru.payts.weatherforecastx.ui.send;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SendViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Just send email at payterms@mail.ru");
    }

    public LiveData<String> getText() {
        return mText;
    }
}