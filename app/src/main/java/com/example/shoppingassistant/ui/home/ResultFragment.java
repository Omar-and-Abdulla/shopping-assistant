package com.example.shoppingassistant.ui.home;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.shoppingassistant.databinding.FragmentResultBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.List;

public class ResultFragment extends Fragment {
    private FragmentResultBinding binding;
    private static final TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    private GraphicOverlay mgraphicOverlay;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        TODO
//        mgraphicOverlay = binding.resultGraphicOverlay;

        String uriArg = ResultFragmentArgs.fromBundle(getArguments()).getUriArg();
        Uri uri = Uri.parse(uriArg);
        Bitmap selectedImage;
        try {
            // uri -> Bitmap, setting image view
            selectedImage = MediaStore.Images.Media.getBitmap(
                    requireContext().getContentResolver(),
                    uri
            );
            binding.resultIngredientImageView.setImageBitmap(selectedImage);
            // detecting text from bitmap
            InputImage image = InputImage.fromBitmap(selectedImage, 0);
            recognizer.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            processTextRecognitionResult(text);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

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
        super.onDestroyView();
        binding = null;
    }
}
