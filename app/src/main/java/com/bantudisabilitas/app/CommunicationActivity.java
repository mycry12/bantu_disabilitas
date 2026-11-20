
package com.bantudisabilitas.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class CommunicationActivity extends AppCompatActivity {

    private EditText etPesan;
    private TextView tvHasilSuara;
    private TextToSpeech textToSpeech;

    private ActivityResultLauncher<Intent> speechLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);

        etPesan = findViewById(R.id.etPesan);
        tvHasilSuara = findViewById(R.id.tvHasilSuara);

        TextView btnKembali = findViewById(R.id.btnKembaliKomunikasi);
        TextView btnBicara = findViewById(R.id.btnBicara);
        TextView btnDengarkan = findViewById(R.id.btnDengarkan);
        TextView btnHapus = findViewById(R.id.btnHapus);
        TextView btnSalin = findViewById(R.id.btnSalin);

        btnKembali.setOnClickListener(v -> finish());

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(
                        new Locale("id", "ID"));

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this,
                            "Suara Bahasa Indonesia belum tersedia.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        speechLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null) {

                        ArrayList<String> hasil =
                                result.getData().getStringArrayListExtra(
                                        RecognizerIntent.EXTRA_RESULTS);

                        if (hasil != null && !hasil.isEmpty()) {
                            etPesan.setText(hasil.get(0));
                            tvHasilSuara.setText(
                                    "Hasil pengenalan: " + hasil.get(0));
                        }
                    }
                });

        btnBicara.setOnClickListener(v -> bacakanTeks());
        btnDengarkan.setOnClickListener(v -> mulaiPengenalanSuara());

        btnHapus.setOnClickListener(v -> {
            etPesan.setText("");
            tvHasilSuara.setText(
                    "Hasil pengenalan: belum ada suara yang diubah menjadi teks.");
        });

        btnSalin.setOnClickListener(v -> salinTeks());

        findViewById(R.id.btnPesanBantu).setOnClickListener(v ->
                isiPesan("Tolong bantu saya."));

        findViewById(R.id.btnPesanTerimaKasih).setOnClickListener(v ->
                isiPesan("Terima kasih."));

        findViewById(R.id.btnPesanTidakMengerti).setOnClickListener(v ->
                isiPesan("Maaf, saya belum mengerti."));
    }

    private void isiPesan(String pesan) {
        etPesan.setText(pesan);
        etPesan.setSelection(etPesan.getText().length());
    }

    private void bacakanTeks() {
        String pesan = etPesan.getText().toString().trim();

        if (pesan.isEmpty()) {
            Toast.makeText(this, "Ketik pesan terlebih dahulu.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textToSpeech != null) {
            textToSpeech.speak(pesan, TextToSpeech.QUEUE_FLUSH,
                    null, "cyla_communication");
        }
    }

    private void mulaiPengenalanSuara() {
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Silakan ucapkan pesan Anda");

        try {
            speechLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Fitur pengenalan suara tidak tersedia di perangkat ini.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void salinTeks() {
        String pesan = etPesan.getText().toString().trim();

        if (pesan.isEmpty()) {
            Toast.makeText(this, "Tidak ada teks untuk disalin.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        if (clipboard != null) {
            clipboard.setPrimaryClip(
                    ClipData.newPlainText("Pesan Cyla Care", pesan));

            Toast.makeText(this, "Teks berhasil disalin.",
                    Toast.LENGTH_SHORT).show();
        }
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