package com.andre_gt.newnaviku.mobility;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andre_gt.newnaviku.R;

import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private TextView resultTextView;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        scannerView = findViewById(R.id.scannerView);
        resultTextView = findViewById(R.id.resultTextView);

        // Inisialisasi TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(Locale.US);
                if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                        langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Handle error
                }
            } else {
                // Handle error
            }
        });

        // Menambahkan OnClickListener pada resultTextView
        resultTextView.setOnClickListener(v -> {
            String text = resultTextView.getText().toString();
            if (!text.isEmpty()) {
                speakOut(text);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scannerView == null) {
            scannerView = findViewById(R.id.scannerView);
        }
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(com.google.zxing.Result result) {
        // Handle result
        String scanResult = result.getText();

        // Tampilkan hasil ke TextView
        resultTextView.setText(scanResult);

        // Munculkan suara untuk hasil pemindaian
        textToSpeech.speak(scanResult, TextToSpeech.QUEUE_FLUSH, null, null);

        // Getar perangkat
        vibrate();

        // Beep default
        beep();

        // Restart pemindaian setelah beberapa detik
        scannerView.resumeCameraPreview(this);

        // Repeat the sound after a delay (e.g., 3 seconds)
        int delayMillis = 3000; // 3 seconds
        new Handler().postDelayed(() -> textToSpeech.speak(scanResult, TextToSpeech.QUEUE_FLUSH, null, null), delayMillis);
    }

    // Metode untuk getar perangkat
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(500); // 500 milliseconds
        }
    }

    // Metode untuk beep default
    private void beep() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 200);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 500); // 150 milliseconds
    }

    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        // Release TextToSpeech
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
