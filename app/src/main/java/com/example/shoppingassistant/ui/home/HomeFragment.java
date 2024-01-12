package com.example.shoppingassistant.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.shoppingassistant.databinding.FragmentHomeBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    // camera permission string from manifest
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    // int for front facing camera
    private static final int BACK_CAMERA = CameraSelector.LENS_FACING_BACK;
    private ProcessCameraProvider cameraProvider;

    /**
     * <String[]>: To make it extendable for multiple permissions
     */
    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> o) {
                    if (Boolean.TRUE.equals(o.get(CAMERA_PERMISSION))) {
                        startCamera();
                    } else {
                        Toast t = new Toast(getContext());
                        t.setText("Need camera for detection.");
                        t.show();
                    }
                }
            }
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // getting camera permission
        if (hasPermission(CAMERA_PERMISSION)) {
            System.out.println("Have already gotten permission");
            startCamera();
        } else {
            requestPermissionLauncher.launch(new String[]{CAMERA_PERMISSION});
        }

        // pick a picture and send URI to ResultFragment
        ActivityResultLauncher<Intent> launchGalleryActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            String uriArg = data.getData().toString();
                            HomeFragmentDirections.ActionNavHomeToNavResult action = HomeFragmentDirections.actionNavHomeToNavResult(uriArg);
                            Navigation.findNavController(root).navigate(action);
                        }
                    }
                }
        );

        binding.homeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launchGalleryActivity.launch(intent);
            }
        });

        return root;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // get singleton camera provider
                    cameraProvider = cameraProviderFuture.get();
                    // local function used to bind use cases foa a camera provider and
                    // selects a camera of choice
                    bindUseCasesAndCamera(cameraProvider, BACK_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private boolean hasPermission(String permissionName) {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                permissionName
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void bindUseCasesAndCamera(ProcessCameraProvider cameraProvider, int cameraType) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.cameraView.getSurfaceProvider());
//        ImageCapture imageCapture = new ImageCapture.Builder().build();
//        imageCapture.t
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(cameraType).build();
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
    }

    @Override
    public void onDestroyView() {
        if (cameraProvider != null)
            cameraProvider.unbindAll();
        super.onDestroyView();
        binding = null;
    }
}