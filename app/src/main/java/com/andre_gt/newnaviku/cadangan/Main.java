/*package com.andre_gt.newnaviku;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.andre_gt.newnaviku.mobility.HomeMobilityActivity;
import com.andre_gt.newnaviku.text_recognition.HomeTextActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton qrCodeButton = findViewById(R.id.qrCodeButton);
        ImageButton textRecognitionButton = findViewById(R.id.trButton);

        qrCodeButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HomeMobilityActivity.class));
        });

        textRecognitionButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HomeTextActivity.class));
        });
    }
}






<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">


    <ImageButton
        android:id="@+id/qrCodeButton"
        android:layout_width="81dp"
        android:layout_height="81dp"
        android:layout_marginTop="20dp"
        android:layout_centerHorizontal="true"
        android:adjustViewBounds="false"
        android:background="@drawable/icon_mobility" />
    <TextView
        android:id="@+id/txtCodeButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Mobility"
        android:layout_below="@+id/qrCodeButton"
        android:layout_centerHorizontal="true" />


    <ImageButton
        android:id="@+id/trButton"
        android:layout_width="81dp"
        android:layout_height="81dp"
        android:layout_below="@+id/txtCodeButton"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="20dp"
        android:adjustViewBounds="false"
        android:background="@drawable/icon_text_recognition" />
    <TextView
        android:id="@+id/txt_trButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Text Recognition"
        android:layout_below="@+id/trButton"
        android:layout_centerHorizontal="true" />

</RelativeLayout>
 */