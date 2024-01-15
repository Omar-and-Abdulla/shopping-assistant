package com.example.shoppingassistant.ui.home;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<Bitmap> image;

    public HomeViewModel() {
        image = new MutableLiveData<>();
    }

    public MutableLiveData<Bitmap> getImage() {
        return image;
    }

    public void setImage(MutableLiveData<Bitmap> image) {
        this.image = image;
    }
}