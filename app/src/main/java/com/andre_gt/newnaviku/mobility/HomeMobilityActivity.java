package com.andre_gt.newnaviku.mobility;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.andre_gt.newnaviku.R;

public class HomeMobilityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_mobility);

        Button generateQRButton = findViewById(R.id.QRCodeGeneratorBtn);
        Button scanQRButton = findViewById(R.id.QRCodeScannerBtn);

        generateQRButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeMobilityActivity.this, GenerateCodeActivity.class);
            startActivity(intent);
        });

        scanQRButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeMobilityActivity.this, ScannerActivity.class);
            startActivity(intent);
        });

    }
}