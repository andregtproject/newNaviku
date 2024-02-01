package com.andre_gt.newnaviku.VoiceMate;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andre_gt.newnaviku.R;

import java.util.ArrayList;
import java.util.Locale;

public class GenerateTextActivity extends AppCompatActivity {

    private TextView textResult;
    private ImageButton btnSpeak;
    private Button btnShare, btnCopy;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_text);

        textResult = findViewById(R.id.txtSpeechInput);
        btnShare = findViewById(R.id.shareButton);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnCopy = findViewById(R.id.copyButton);

        btnCopy.setOnClickListener(v -> copyTextToClipboard(textResult.getText().toString()));

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareText();
            }
        });

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    private void copyTextToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.copy_txt), text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, getString(R.string.copy_desc), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.copy_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareText() {
        String textToShare = textResult.getText().toString();

        if (!textToShare.isEmpty()) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);

            String shareTitle = getString(R.string.share_title);
            String shareVia = getString(R.string.share_via);

            startActivity(Intent.createChooser(shareIntent, shareTitle + " " + shareVia));
        } else {
            Toast.makeText(this, getString(R.string.no_text_to_share), Toast.LENGTH_SHORT).show();
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textResult.setText(result.get(0));
                }
                break;
            }

        }
    }


}
