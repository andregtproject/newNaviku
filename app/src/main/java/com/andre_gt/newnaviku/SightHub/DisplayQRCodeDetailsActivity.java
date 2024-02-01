package com.andre_gt.newnaviku.SightHub;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andre_gt.newnaviku.R;

import java.io.File;

public class DisplayQRCodeDetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView detailsTextView;
    private String qrCodePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qrcode_details);

        imageView = findViewById(R.id.imageView);
        detailsTextView = findViewById(R.id.detailsTextView);
        qrCodePath = getIntent().getStringExtra("qrCodePath");

        Bitmap bitmap = BitmapFactory.decodeFile(qrCodePath);
        imageView.setImageBitmap(bitmap);

        String translationText = getTranslationForQRCode(qrCodePath);
        detailsTextView.setText(translationText);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fileToDelete = new File(qrCodePath);
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private String getTranslationForQRCode(String qrCodePath) {
        return qrCodePath;
    }
}


