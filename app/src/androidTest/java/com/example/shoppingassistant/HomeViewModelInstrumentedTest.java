package com.example.shoppingassistant;

import static org.junit.Assert.*;

import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import com.example.shoppingassistant.ui.home.HomeViewModel;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class HomeViewModelInstrumentedTest {
    HomeViewModel hm;
    File filesDirectory;

    @Before
    public void setup() {
        hm = new HomeViewModel();
        filesDirectory = ApplicationProvider.getApplicationContext().getDataDir();
    }

    @Test
    public void test_getSetUri() {
        assertNull(hm.getUri());
    }

    @Test
    public void test_setUri() {}
}
