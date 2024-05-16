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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.nio.ByteBuffer;
import java.util.List;

public class ResultFragment extends Fragment {
    class BarcodeRequestCallback extends UrlRequest.Callback {

        @Override
        public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {

        }

        @Override
        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {

        }

        @Override
        public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {

        }

        @Override
        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
            Log.i(LOG_TAG, "Request completed");
        }

        @Override
        public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {

        }
    }
    private FragmentResultBinding binding;
    private static final TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//    private GraphicOverlay mgraphicOverlay;
    private static final String LOG_TAG = "ResultFragment";
    private CronetEngine cronetEngine;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        HomeViewModel homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        cronetEngine = new CronetEngine.Builder(requireContext())
                .build();


//        TODO
//        mgraphicOverlay = binding.resultGraphicOverlay;

        // get image and apply appropriate rotation if needed
        Uri uri = homeViewModel.getUriIngredient();

        try {
            Bitmap image = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
            // setting image in imageview
            binding.resultIngredientImageView.setImageBitmap(image);
//            getAndSetTextFromImage(image);
            getBarcode(image);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in URI to BitMap conversion", e);
        }
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

    public void getBarcode(Bitmap image) {
        BarcodeScannerOptions bcScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E
                )
                .build();
        InputImage inputImage = InputImage.fromBitmap(image, 0);
        BarcodeScanner barcodeScanner  = BarcodeScanning.getClient(bcScannerOptions);
        Task<List<Barcode>> result = barcodeScanner.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        Log.d(LOG_TAG, barcodes.size() + "");
                        String barcode = barcodes.get(0).getRawValue();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_TAG, e.toString());
                    }
                });
    }

    public void getAndSetTextFromImage(Bitmap image) {
        InputImage inputImage = InputImage.fromBitmap(image, 0);
        recognizer.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        processTextRecognitionResult(text);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_TAG, "Failed to extract text from Image");
                        e.printStackTrace();
                    }
                });
    }

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
                StringBuilder s= new StringBuilder();
                Log.d(LOG_TAG, String.format("This is block %d\n%s\n\n", i, blocks.get(i).getText()));
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
//        HomeViewModel homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
//        Uri uri = homeViewModel.getUri();
//        if (uri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            requireActivity().getContentResolver().delete(uri, null);
//        }
        super.onDestroyView();
        binding = null;
    }
}
