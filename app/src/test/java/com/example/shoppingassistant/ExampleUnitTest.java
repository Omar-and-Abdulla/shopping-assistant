package com.example.shoppingassistant;

import org.junit.Test;

import static org.junit.Assert.*;

import android.net.Uri;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.example.shoppingassistant.ui.home.HomeViewModel;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_HomeViewModel() {

        HomeViewModel hm = new ViewModelProvider(new ViewModelStore(), new ViewModelProvider.AndroidViewModelFactory()).get(HomeViewModel.class);
        assertNull(hm.getUri());

        String testString = "dummy_uri";
        assertThrows(RuntimeException.class, () -> {
            Uri.parse(testString);
        });
    }
}