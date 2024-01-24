package com.example.shoppingassistant.ui.home;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.camera.core.ImageProxy;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<Uri> image;

//    private MutableLiveData<Integer> rotatedDegree;

    public HomeViewModel() {
        image = new MutableLiveData<>();
//        rotatedDegree = new MutableLiveData<>(0);
    }

    public Uri getImage() {
        return image.getValue();
    }

    public void setImage(Uri image) {
        this.image.setValue(image);
    }

//    public Integer getRotatedDegree() {
//        return rotatedDegree.getValue();
//    }
//
//    public void setRotatedDegree(Integer rotatedDegree) {
//        this.rotatedDegree.setValue(rotatedDegree);
//    }
}