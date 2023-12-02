package com.andre_gt.newnaviku.text_recognition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.andre_gt.newnaviku.R;

public class HomeTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_text);

        Button generateTextButton = findViewById(R.id.TextGeneratorBtn);
        Button scanTextButton = findViewById(R.id.TextScannerBtn);

        generateTextButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeTextActivity.this, GenerateTextActivity.class);
            startActivity(intent);
        });

        scanTextButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeTextActivity.this, TextRecognitionActivity.class);
            startActivity(intent);
        });

    }
}