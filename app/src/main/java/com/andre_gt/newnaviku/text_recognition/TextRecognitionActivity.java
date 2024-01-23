package com.andre_gt.newnaviku.text_recognition;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.andre_gt.newnaviku.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Locale;

public class TextRecognitionActivity extends AppCompatActivity {

    private CameraSource cameraSource;
    private TextToSpeech textToSpeech;
    private TextView textResult;
    private boolean isCapturing = false;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        textResult = findViewById(R.id.textResult);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            initializeCamera();
        }

        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });

        Button copyButton = findViewById(R.id.copyButton);
        Button shareButton = findViewById(R.id.shareButton);


        copyButton.setOnClickListener(v -> copyTextToClipboard(textResult.getText().toString()));
        shareButton.setOnClickListener(v -> shareText(textResult.getText().toString()));

        // Menambahkan OnClickListener untuk teks yang berhasil diakuisisi
        textResult.setOnClickListener(v -> {
            // Memanggil kembali fungsi untuk berbicara ketika teks di layar disentuh
            if (!textResult.getText().toString().isEmpty()) {
                speakOut(textResult.getText().toString());
            }
        });
    }

    private void initializeCamera() {
        SurfaceView cameraView = findViewById(R.id.cameraView);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            // Handle the error
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            cameraView.getHolder().addCallback(new CameraSourcePreviewCallback());
            textRecognizer.setProcessor(new TextProcessor());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    private class CameraSourcePreviewCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            try {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TextRecognitionActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            1);
                    return;
                }
                cameraSource.start(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            // Not needed in this example
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            cameraSource.stop();
        }
    }

    private class TextProcessor implements Detector.Processor<TextBlock> {

        @Override
        public void release() {
            // Release resources
        }

        @Override
        public void receiveDetections(@NonNull Detector.Detections<TextBlock> detections) {
            if (isCapturing) {
                isCapturing = false;
                runOnUiThread(() -> {/* Remove any related UI changes */});
            }

            SparseArray<TextBlock> items = detections.getDetectedItems();
            if (items.size() != 0) {
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i < items.size(); ++i) {
                    TextBlock item = items.valueAt(i);
                    stringBuilder.append(item.getValue());
                    stringBuilder.append("\n");
                }

                processTextRecognitionResult(stringBuilder.toString());
            }
        }
    }

    private void processTextRecognitionResult(String recognizedText) {
        runOnUiThread(() -> textResult.setText(recognizedText));
        speakOut(recognizedText);
        playFeedback();
    }

    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void playFeedback() {
        playBeep();
        vibrate();
    }

    private void playBeep() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 200);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 300); // 150 milliseconds
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            // Deprecated in API 26
            vibrator.vibrate(200);
        }
    }

    private void copyTextToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share Text"));
    }
}