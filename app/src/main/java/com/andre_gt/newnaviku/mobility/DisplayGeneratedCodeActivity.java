package com.andre_gt.newnaviku.mobility;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andre_gt.newnaviku.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DisplayGeneratedCodeActivity extends AppCompatActivity implements QRCodeAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private QRCodeAdapter qrCodeAdapter;
    private List<String> savedQRCodePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_generated_code);

        recyclerView = findViewById(R.id.recyclerView);
        savedQRCodePaths = getSavedQRCodePaths(); // Get paths of saved QR codes
        qrCodeAdapter = new QRCodeAdapter(savedQRCodePaths, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(qrCodeAdapter);
    }

    // Get paths of saved QR codes from storage
    private List<String> getSavedQRCodePaths() {
        List<String> paths = new ArrayList<>();
        String directoryName = "Naviku";
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    paths.add(file.getAbsolutePath());
                }
            }
        }

        return paths;
    }

    // Implement the onItemClick method from QRCodeAdapter.OnItemClickListener
    @Override
    public void onItemClick(int position) {
        // Handle the click on a QR code in the list (e.g., share or download)
        String selectedQRCodePath = savedQRCodePaths.get(position);

        // Example: Display a toast message for demonstration
        Toast.makeText(this, "Clicked on item at position " + position, Toast.LENGTH_SHORT).show();
    }
}
