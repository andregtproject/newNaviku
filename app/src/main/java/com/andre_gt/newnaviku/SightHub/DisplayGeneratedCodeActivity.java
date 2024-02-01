package com.andre_gt.newnaviku.SightHub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
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
        savedQRCodePaths = getSavedQRCodePaths();
        qrCodeAdapter = new QRCodeAdapter(savedQRCodePaths, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(qrCodeAdapter);
    }

    private List<String> getSavedQRCodePaths() {
        List<String> paths = new ArrayList<>();
        String directoryName = getString(R.string.app_name);
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

    @Override
    public void onItemClick(int position) {
        String selectedQRCodePath = savedQRCodePaths.get(position);

        Intent intent = new Intent(this, DisplayQRCodeDetailsActivity.class);
        intent.putExtra("qrCodePath", selectedQRCodePath);
        startActivityForResult(intent, 1);
    }

    public void updateData(List<String> newData) {
        savedQRCodePaths.clear();
        savedQRCodePaths.addAll(newData);
        qrCodeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            savedQRCodePaths = getSavedQRCodePaths();
            updateData(savedQRCodePaths);
        }
    }
}
