package com.andre_gt.newnaviku.SightHub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.andre_gt.newnaviku.R;

public class HomeMobilityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_mobility, container, false);

        Button generateQRButton = view.findViewById(R.id.QRCodeGeneratorBtn);
        Button scanQRButton = view.findViewById(R.id.QRCodeScannerBtn);

        generateQRButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), GenerateCodeActivity.class);
            startActivity(intent);
        });

        scanQRButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ScannerActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
