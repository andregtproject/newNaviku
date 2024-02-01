package com.andre_gt.newnaviku.SightHub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.andre_gt.newnaviku.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class GenerateCodeActivity extends AppCompatActivity {

    private ImageView qrImageView;
    private EditText dataEditText;
    private Button generateButton;
    private Button saveButton;
    private Button shareButton;
    private Button showGeneratedCodeButton;
    private ImageButton speakButton;

    private Bitmap logoBitmap;

    private static final int SPEECH_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        qrImageView = findViewById(R.id.qrImageView);
        dataEditText = findViewById(R.id.dataEditText);
        generateButton = findViewById(R.id.generateButton);
        saveButton = findViewById(R.id.saveButton);
        shareButton = findViewById(R.id.shareButton);
        showGeneratedCodeButton = findViewById(R.id.showGeneratedCodeButton);
        speakButton = findViewById(R.id.speakButton);

        logoBitmap = getResizedLogo(R.drawable.logo_navigt);

        setButtonVisibility(View.INVISIBLE);

        dataEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString().trim();
                generateButton.setEnabled(newText.length() > 0);

                if (newText.length() == 0) {
                    qrImageView.setImageBitmap(null);
                    setButtonVisibility(View.INVISIBLE);
                }
            }
        });

        generateButton.setOnClickListener(v -> generateQRCode());
        saveButton.setOnClickListener(v -> saveQRCode());
        shareButton.setOnClickListener(v -> shareQRCode());
        showGeneratedCodeButton.setOnClickListener(v -> showGeneratedCode());
        speakButton.setOnClickListener(view -> startSpeechToText());
    }

    private void setButtonVisibility(int visibility) {
        saveButton.setVisibility(visibility);
        shareButton.setVisibility(visibility);
        showGeneratedCodeButton.setVisibility(visibility);
    }

    private void generateQRCode() {
        String data = dataEditText.getText().toString();
        try {
            int qrSize = 512;
            Bitmap qrCodeBitmap = generateQRCode(data, qrSize);

            if (qrCodeBitmap != null) {
                String appName = getString(R.string.app_name);
                Bitmap resultBitmap = addLogoAndNameToQRCode(qrCodeBitmap, logoBitmap, appName);

                qrImageView.setImageBitmap(resultBitmap);

                Toast.makeText(this, getString(R.string.qr_generated_success), Toast.LENGTH_SHORT).show();

                setButtonVisibility(View.VISIBLE);
            } else {
                qrImageView.setImageBitmap(null);
                setButtonVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            Log.e(getString(R.string.log_tag_generator), getString(R.string.error_generating_qr, e.getMessage()));
        }
    }

    private void showGeneratedCode() {
        String generatedText = dataEditText.getText().toString().trim();
        if (!generatedText.isEmpty()) {
            Intent intent = new Intent(this, DisplayGeneratedCodeActivity.class);
            intent.putExtra("generatedText", generatedText);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.generate_save_first), Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap generateQRCode(String data, int size) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixels[y * width + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }

            Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            qrBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            return qrBitmap;
        } catch (Exception e) {
            Log.e(getString(R.string.log_tag_generator), getString(R.string.error_generating_qr, e.getMessage()));
            return null;
        }
    }

    private Bitmap addLogoAndNameToQRCode(Bitmap qrCodeBitmap, Bitmap logo, String appName) {
        int qrCodeSize = qrCodeBitmap.getWidth();
        int logoSize = qrCodeSize / 7;
        int margin = -15;
        int bottomMargin = -10;

        Bitmap resultBitmap = qrCodeBitmap.copy(qrCodeBitmap.getConfig(), true);
        Canvas canvas = new Canvas(resultBitmap);

        int left = (int) ((qrCodeSize - logoSize) / 2.5);
        int top = qrCodeSize - logoSize - margin - bottomMargin;
        canvas.drawBitmap(logo, left, top, null);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(24);
        int textX = left + logoSize + margin;
        float textY = (float) (top + (logoSize / 2.5) + (paint.getTextSize() / 2.5));
        canvas.drawText(appName, textX, textY, paint);

        return resultBitmap;
    }

    private void saveQRCode() {
        String generatedText = dataEditText.getText().toString().trim();

        if (!generatedText.isEmpty()) {
            Bitmap qrCodeBitmap = ((BitmapDrawable) qrImageView.getDrawable()).getBitmap();

            String directoryName = getString(R.string.app_name);
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = generatedText + ".png";
            File file = new File(directory, fileName);

            try {
                OutputStream outputStream = new FileOutputStream(file);
                qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                Toast.makeText(this, getString(R.string.qr_saved_to, file.getAbsolutePath()), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setButtonVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, getString(R.string.generate_save_first), Toast.LENGTH_LONG).show();        }
    }

    private void shareQRCode() {
        Bitmap qrCodeBitmap = ((BitmapDrawable) qrImageView.getDrawable()).getBitmap();

        if (qrCodeBitmap != null) {
            try {
                String fileName = "QRCode.png";

                // Menggunakan FileProvider untuk Android 10 ke atas
                File file = new File(getExternalCacheDir(), fileName);
                file.createNewFile();
                OutputStream outputStream = new FileOutputStream(file);
                qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();

                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_qr)));

                setButtonVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.error_share) + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.generate_share_code), Toast.LENGTH_LONG).show();
        }
    }



    private Bitmap getResizedLogo(int resId) {
        int widthPx = (int) (0.3 * getResources().getDisplayMetrics().xdpi / 2.54);
        int heightPx = (int) (0.3 * getResources().getDisplayMetrics().ydpi / 2.54);

        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), resId);

        return Bitmap.createScaledBitmap(originalBitmap, widthPx, heightPx, false);
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String spokenText = matches.get(0);
                dataEditText.setText(spokenText);
            }
        }
    }
}
