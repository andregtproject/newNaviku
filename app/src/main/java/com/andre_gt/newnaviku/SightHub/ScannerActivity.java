package com.andre_gt.newnaviku.SightHub;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andre_gt.newnaviku.R;

import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private TextView resultTextView;
    private TextToSpeech textToSpeech;
    private ToggleButton flashToggleButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        scannerView = findViewById(R.id.scannerView);
        resultTextView = findViewById(R.id.resultTextView);

        scannerView = findViewById(R.id.scannerView);
        scannerView.setAspectTolerance(0.5f);
        scannerView.setFlash(false);

        flashToggleButton = findViewById(R.id.flashToggleButton);
        flashToggleButton.setOnClickListener(v -> toggleFlash());

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(Locale.US);
                if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                        langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                } else {
                    speakOut(getString(R.string.welcome_scanner));
                }
            }
        });

        resultTextView.setOnClickListener(v -> {
            String text = resultTextView.getText().toString();
            if (!text.isEmpty()) {
                speakOut(text);
            }
        });
    }

    private void toggleFlash() {
        boolean isFlashOn = scannerView.getFlash();
        if (isFlashOn) {
            scannerView.setFlash(false);
            speakOut(getString(R.string.turn_off_flash));
        } else {
            scannerView.setFlash(true);
            speakOut(getString(R.string.turn_on_flash));
        }
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
        String scanResult = result.getText();

        resultTextView.setText(scanResult);

        textToSpeech.speak(scanResult, TextToSpeech.QUEUE_FLUSH, null, null);

        vibrate();
        beep();
        scannerView.resumeCameraPreview(this);

        int delayMillis = 3000; // 3 seconds
        new Handler().postDelayed(() -> textToSpeech.speak(scanResult, TextToSpeech.QUEUE_FLUSH, null, null), delayMillis);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(1000);
        }
    }

    private void beep() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 200);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
    }

    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
