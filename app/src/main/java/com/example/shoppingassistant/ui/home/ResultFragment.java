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
    private TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
            String text = texts.getText();
            binding.resultIngredientDetail.setText(text);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
