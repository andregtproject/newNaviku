package com.andre_gt.newnaviku.VoiceMate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.andre_gt.newnaviku.R;

public class HomeTextFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_text, container, false);

        Button generateTextButton = view.findViewById(R.id.TextGeneratorBtn);
        Button scanTextButton = view.findViewById(R.id.TextScannerBtn);

        generateTextButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), GenerateTextActivity.class);
            startActivity(intent);
        });

        scanTextButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), TextRecognitionActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
