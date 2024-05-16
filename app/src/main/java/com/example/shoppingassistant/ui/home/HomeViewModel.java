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

    private MutableLiveData<Uri> uriIngredient;
    private MutableLiveData<Uri> uriBarcode;

    public HomeViewModel() {
        uriIngredient = new MutableLiveData<>();
        uriBarcode = new MutableLiveData<>();
    }

    public Uri getUriIngredient() {
        return uriIngredient.getValue();
    }

    public void setUriIngredient(Uri uriIngredient) {
        this.uriIngredient.setValue(uriIngredient);
    }

    public MutableLiveData<Uri> getUriBarcode() {
        return uriBarcode;
    }

    public void setUriBarcode(MutableLiveData<Uri> uriBarcode) {
        this.uriBarcode = uriBarcode;
    }
}