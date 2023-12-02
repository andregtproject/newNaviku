package com.andre_gt.newnaviku.mobility;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andre_gt.newnaviku.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class GenerateCodeActivity extends AppCompatActivity {

    private ImageView qrImageView;
    private EditText dataEditText;
    private Button generateButton;
    private Button saveButton;
    private Button shareButton;

    // Ganti dengan logo aplikasi Anda (pastikan sudah siapkan logo di res/drawable)
    private Bitmap logoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        qrImageView = findViewById(R.id.qrImageView);
        dataEditText = findViewById(R.id.dataEditText);
        generateButton = findViewById(R.id.generateButton);
        saveButton = findViewById(R.id.saveButton);
        shareButton = findViewById(R.id.shareButton);

        // Inisialisasi logo aplikasi dan mengatur ukuran menjadi 2 cm x 2 cm
        logoBitmap = getResizedLogo(R.drawable.qrcode); // Gantilah dengan sumber logo aplikasi Anda

        // Initial visibility state
        setButtonVisibility(View.INVISIBLE);

        // Tambahkan pemantauan perubahan pada EditText
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
                generateButton.setEnabled(newText.length() > 0); // Enable/disable generateButton based on text length

                if (newText.length() == 0) {
                    qrImageView.setImageBitmap(null); // Clear QR code if text is empty
                    setButtonVisibility(View.INVISIBLE); // Hide buttons if text is empty
                }
            }
        });

        generateButton.setOnClickListener(v -> generateQRCode());
        saveButton.setOnClickListener(v -> saveQRCode());
        shareButton.setOnClickListener(v -> shareQRCode());
    }

    private void setButtonVisibility(int visibility) {
        saveButton.setVisibility(visibility);
        shareButton.setVisibility(visibility);
    }

    private void generateQRCode() {
        String data = dataEditText.getText().toString();
        try {
            int qrSize = 512;
            Bitmap qrCodeBitmap = generateQRCode(data, qrSize);

            if (qrCodeBitmap != null) {
                String appName = "Naviku";
                Bitmap resultBitmap = addLogoAndNameToQRCode(qrCodeBitmap, logoBitmap, appName);

                qrImageView.setImageBitmap(resultBitmap);

                // Tampilkan pesan jika berhasil membuat QR Code
                Toast.makeText(this, "QR Code generated successfully", Toast.LENGTH_SHORT).show();

                // Update visibility after generating QR code
                setButtonVisibility(View.VISIBLE);
            } else {
                // Clear image view and hide buttons if QR code generation fails
                qrImageView.setImageBitmap(null);
                setButtonVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            Log.e("GeneratorActivity", "Error generating QR code: " + e.getMessage());
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
            Log.e("GeneratorActivity", "Error generating QR code: " + e.getMessage());
            return null;
        }
    }

    private Bitmap addLogoAndNameToQRCode(Bitmap qrCodeBitmap, Bitmap logo, String appName) {
        int qrCodeSize = qrCodeBitmap.getWidth();
        int logoSize = qrCodeSize / 7;
        int margin = -5;
        int bottomMargin = -5;

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
        Bitmap qrCodeBitmap = ((BitmapDrawable) qrImageView.getDrawable()).getBitmap();

        if (qrCodeBitmap != null) {
            String fileName = "QRCode.png";
            try {
                File file = new File(getCacheDir(), fileName);
                OutputStream outputStream = new FileOutputStream(file);
                qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                Toast.makeText(this, "QR Code saved to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Update visibility after saving QR code
            setButtonVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Generate a QR Code first before saving.", Toast.LENGTH_LONG).show();
        }
    }

    private void shareQRCode() {
        Bitmap qrCodeBitmap = ((BitmapDrawable) qrImageView.getDrawable()).getBitmap();

        if (qrCodeBitmap != null) {
            String fileName = "QRCode.png";
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), qrCodeBitmap, fileName, null);
            Uri uri = Uri.parse(path);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "Share QR Code"));

            // Update visibility after sharing QR code
            setButtonVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Generate a QR Code first before sharing.", Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap getResizedLogo(int resId) {
        // Konversi ukuran dalam cm ke piksel
        int widthPx = (int) (0.3 * getResources().getDisplayMetrics().xdpi / 2.54);
        int heightPx = (int) (0.3 * getResources().getDisplayMetrics().ydpi / 2.54);

        // Mengambil logo dari sumber Drawable
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), resId);

        // Meresize logo ke ukuran yang diinginkan
        return Bitmap.createScaledBitmap(originalBitmap, widthPx, heightPx, false);
    }
}
