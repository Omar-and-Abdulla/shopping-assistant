package com.example.shoppingassistant.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.shoppingassistant.databinding.FragmentHomeBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Navigates to result fragment 2 places -
 * 1. When selecting an image after clicking gallery button
 * 2. When taking picture of an image
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private final String LOG_TAG = "HomeFragment";

    // camera permission string
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    // Media-Image permission string
    private static final String MEDIA_IMAGE_READ_PERMISSION = Manifest.permission.READ_MEDIA_IMAGES;

    // int for front facing camera
    private static final int BACK_CAMERA = CameraSelector.LENS_FACING_BACK;
    private ProcessCameraProvider cameraProvider;
    ImageCapture imageCapture;
    private Camera camera;
    private Uri tempCopy;

    /**
     * <String[]>: To make it extendable for multiple permissions
     */
    private final ActivityResultLauncher<String[]> requestMultiPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> o) {
                    if (Boolean.TRUE.equals(o.get(CAMERA_PERMISSION))) {
                        startCamera();
                    }
                }
            }
    );

    // To launch gallery
    ActivityResultLauncher<Intent> launchGalleryActivity;

    // to launch Crop Image
    private ActivityResultLauncher<CropImageContractOptions> cropLauncher;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // getting camera permission
        if (hasPermission(CAMERA_PERMISSION)) {
            startCamera();
        } else {
            requestMultiPermissionLauncher.launch(new String[]{CAMERA_PERMISSION});
        }

        //  Initializing cropping launcher
        cropLauncher = registerForActivityResult(
                new CropImageContract(),
                result -> {
                    if (result.isSuccessful()) {
                        Uri uri = result.getUriContent();
                        homeViewModel.setUri(uri);
                        Navigation.findNavController(root).navigate(HomeFragmentDirections.actionNavHomeToNavResult());
                    } else {
                        Log.e(LOG_TAG, "Couldn't crop image for some in (ActivityResultLauncher<CropImageContractOptions>) cropLauncher");
                    }
                }
        );

        // Activity Result launcher for gallery
        launchGalleryActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri uri = data.getData();
                            try {
                                launchCropper(uri);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(LOG_TAG, "Couldn't crop selected image.");
                            }
                        }
                    }
                }
        );
        binding.homeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // no need permission since accessing internal storage
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                launchGalleryActivity.launch(intent);
            }
        });

        // taking picture from camera
        binding.takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        super.onCaptureSuccess(image);
                        Bitmap rotatedImage = rotateBitmap(image.toBitmap(), image.getImageInfo().getRotationDegrees());
                        tempCopy = saveImage(rotatedImage);
                        launchCropper(tempCopy);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                        exception.printStackTrace();
                        Toast.makeText(
                                requireContext(),
                                "Couldn't retrieve captured image",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return root;
    }

    private void launchCropper(Uri uri) {
        // image options
        CropImageOptions imageOptions = new CropImageOptions();
        CropImageContractOptions imageContractOptions = new CropImageContractOptions(uri, imageOptions);
        cropLauncher.launch(imageContractOptions);
    }

    private Bitmap rotateBitmap(Bitmap source, int rotatedDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotatedDegree);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private Uri saveImage(Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(),
                image,
                generateUniqueFileName(),
                null
        );
        return Uri.parse(path);
    }

    private String generateUniqueFileName() {
        String FILENAME_FORMAT = "yyyyMMdd_HHmmssSSS";
        return new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis());
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
                    camera = configCamera(cameraProvider, BACK_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()));

        // Setting zoom functionality
        ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                ZoomState zs = camera.getCameraInfo().getZoomState().getValue();
                float scale = detector.getScaleFactor();
                camera.getCameraControl().setZoomRatio(scale * zs.getZoomRatio());
                return true;
            }

            @Override
            public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
            }
        };
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(requireContext(), onScaleGestureListener);
        binding.cameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return scaleGestureDetector.onTouchEvent(event);
            }
        });
    }

    private boolean hasPermission(String permissionName) {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                permissionName
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private Camera configCamera(ProcessCameraProvider cameraProvider, int cameraType) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.cameraView.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(cameraType).build();
        return cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageCapture, preview);
    }

    @Override
    public void onDestroyView() {
        if (tempCopy != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            requireActivity().getContentResolver().delete(tempCopy, null);
        if (cameraProvider != null)
            cameraProvider.unbindAll();
        super.onDestroyView();
        binding = null;
    }
}