package com.example.shoppingassistant.ui.home;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Assuming permissions have been granted for accessing media
 */
public class HomeViewModel extends ViewModel {
    private MutableLiveData<Uri> uri;

    public HomeViewModel() {
        uri = new MutableLiveData<>();
    }

    public Uri getUri() {
        return uri.getValue();
    }

    public void setUri(Uri image) {
        this.uri.setValue(image);
    }
}