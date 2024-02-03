package com.example.shoppingassistant.ui.home.util.result;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shoppingassistant.databinding.FragmentResultBinding;
import com.example.shoppingassistant.ui.home.HomeViewModel;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.List;

public class ResultFragment extends Fragment {
    private FragmentResultBinding binding;
    private static final TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//    private GraphicOverlay mgraphicOverlay;
    private static final String LOG_TAG = "ResultFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        HomeViewModel homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

//        TODO
//        mgraphicOverlay = binding.resultGraphicOverlay;

        // get image and apply appropriate rotation if needed
        Uri uri = homeViewModel.getUri();
//        int rotatedDegree = getRotationDegree(uri);
//        Log.d(LOG_TAG, String.format("Rotated degree from result fragment %d", rotatedDegree));

//        if (homeViewModel.getRotatedDegree() != null)
//            rotatedDegree = homeViewModel.getRotatedDegree();
        try {
            Bitmap image = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
            // setting image in imageview
            binding.resultIngredientImageView.setImageBitmap(image);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in URI to BitMap conversion", e);
        }
//        Bitmap image = rotateBitmap(, rotatedDegree);

//        // cropping the image
//        ActivityResultLauncher<Intent> launchCropActivity = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    System.out.println("Reached results");
//                }
//        );
//        Intent i = new Intent("com.android.camera.action.CROP");
//        i.setType("image/*");
//        List<ResolveInfo> list = requireActivity().getPackageManager().queryIntentActivities(i, 0);
//        int size = list.size();
//        if (size == 0) {
//            Toast.makeText(requireContext(),
//                    "Can not find image crop app",
//                    Toast.LENGTH_SHORT).show();
//        } else {
//            i.setData();
//        }

//        // extracting ingredient details
//        InputImage inputImage = InputImage.fromBitmap(image, 0);
//        recognizer.process(inputImage)
//                .addOnSuccessListener(new OnSuccessListener<Text>() {
//                    @Override
//                    public void onSuccess(Text text) {
//                        processTextRecognitionResult(text);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        e.printStackTrace();
//                    }
//                });

        return root;
    }

//    private int getRotationDegree(Uri uri) {
//        InputStream input = null;
//        try {
//            input = requireContext().getContentResolver().openInputStream(uri);
//            if (input != null) {
//                ExifInterface exifInterface = new ExifInterface(input);
//                int orientation = exifInterface.getAttributeInt(
//                        ExifInterface.TAG_ORIENTATION,
//                        ExifInterface.ORIENTATION_NORMAL
//                );
//                return getRotationDegreeFromOrientation(orientation);
//            }
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "Error reading Exif data", e);
//        } finally {
//            try {
//                if (input != null) {
//                    input.close();
//                }
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error closing input stream from URI", e);
//            }
//        }
//        return 0;
//    }
//
//    private int getRotationDegreeFromOrientation(int orientation) {
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                return 90;
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                return 180;
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                return 270;
//            default:
//                return 0;
//        }
//    }

    /**
     * Sets ingretedientDetails <TextView> in layout with
     * extracted text
     * @param texts -> Text extracted in onCreateView
     */
    private void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            binding.resultIngredientDetail.setText("No Texts found");
        } else {

//            TODO - Adding overlay to the existing image view
//            mgraphicOverlay.clear();
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < blocks.size(); ++i) {
                text.append(String.format("This is block %d\n", i));
                text.append(String.format("%s\n\n", blocks.get(i).getText()));
//                List<Text.Line> lines = blocks.get(i).getLines();
//                for (int k = 0; k < lines.size(); ++k) {
//                    List<Text.Element> elements = lines.get(k).getElements();
//                    for (int j = 0; j < elements.size(); ++j) {
//                        GraphicOverlay.Graphic textGraphic = new TextGraphic(mgraphicOverlay, elements.get(j));
//                        mgraphicOverlay.add(textGraphic);
//                    }
//                }
            }
            binding.resultIngredientDetail.setText(text.toString());
        }
    }

    @Override
    public void onDestroyView() {
        HomeViewModel homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        Uri uri = homeViewModel.getUri();
        if (uri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().getContentResolver().delete(uri, null);
        }
        super.onDestroyView();
        binding = null;
    }
}
