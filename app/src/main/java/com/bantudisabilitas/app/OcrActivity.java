
package com.bantudisabilitas.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Locale;

public class OcrActivity extends AppCompatActivity {

    private TextView tvHasilOCR;
    private TextToSpeech textToSpeech;
    private TextRecognizer recognizer;
    private boolean ttsSiap = false;

    private ActivityResultLauncher<Void> kameraLauncher;
    private ActivityResultLauncher<String> galeriLauncher;
    private ActivityResultLauncher<String> izinKameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        // Tombol kembali
        TextView btnKembali = findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(v -> finish());

        Button btnAmbilFoto = findViewById(R.id.btnAmbilFoto);
        Button btnPilihGambar = findViewById(R.id.btnPilihGambar);
        Button btnBacakan = findViewById(R.id.btnBacakan);
        tvHasilOCR = findViewById(R.id.tvHasilOCR);

        recognizer = TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
        );

        kameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        prosesOCR(bitmap);
                    } else {
                        tampilkanPesan(
                                "Foto tidak diambil. Silakan coba lagi."
                        );
                    }
                }
        );

        galeriLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        prosesOCR(uri);
                    }
                }
        );

        izinKameraLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                diberikan -> {
                    if (diberikan) {
                        bukaKamera();
                    } else {
                        tampilkanPesan(
                                "Izin kamera ditolak. Aktifkan izin kamera melalui Pengaturan aplikasi."
                        );
                    }
                }
        );

        btnAmbilFoto.setOnClickListener(v -> periksaIzinKamera());

        btnPilihGambar.setOnClickListener(v ->
                galeriLauncher.launch("image/*")
        );

        btnBacakan.setOnClickListener(v -> bacakanHasil());

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS && textToSpeech != null) {
                int hasilBahasa = textToSpeech.setLanguage(
                        new Locale("id", "ID")
                );

                ttsSiap = hasilBahasa != TextToSpeech.LANG_MISSING_DATA
                        && hasilBahasa != TextToSpeech.LANG_NOT_SUPPORTED;
            }
        });
    }

    private void periksaIzinKamera() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            bukaKamera();
        } else {
            izinKameraLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void bukaKamera() {
        try {
            kameraLauncher.launch(null);
        } catch (Exception e) {
            Log.e("OcrActivity", "Gagal membuka kamera", e);
            tampilkanPesan(
                    "Kamera gagal dibuka: " + e.getMessage()
            );
        }
    }

    private void prosesOCR(Bitmap bitmap) {
        tvHasilOCR.setText("Sedang mengenali teks...");

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        jalankanPengenalan(image);
    }

    private void prosesOCR(Uri uri) {
        tvHasilOCR.setText("Sedang mengenali teks...");

        try {
            InputImage image = InputImage.fromFilePath(this, uri);
            jalankanPengenalan(image);
        } catch (IOException e) {
            Log.e("OcrActivity", "Gagal membuka gambar", e);
            tampilkanPesan("Gagal membuka gambar.");
        }
    }

    private void jalankanPengenalan(InputImage image) {
        tvHasilOCR.setText("Sedang mengenali teks...");

        recognizer.process(image)
                .addOnSuccessListener(hasil -> {
                    StringBuilder teksRapi = new StringBuilder();

                    for (Text.TextBlock blok : hasil.getTextBlocks()) {
                        StringBuilder teksBlok = new StringBuilder();

                        for (Text.Line baris : blok.getLines()) {
                            String teksBaris = baris.getText()
                                    .replaceAll("[\\t ]+", " ")
                                    .trim();

                            if (!teksBaris.isEmpty()) {
                                if (teksBlok.length() > 0) {
                                    teksBlok.append("\n");
                                }

                                teksBlok.append(teksBaris);
                            }
                        }

                        if (teksBlok.length() > 0) {
                            if (teksRapi.length() > 0) {
                                teksRapi.append("\n\n");
                            }

                            teksRapi.append(teksBlok);
                        }
                    }

                    String hasilTeks = teksRapi.toString().trim();

                    if (hasilTeks.isEmpty()) {
                        tvHasilOCR.setText(
                                "Tidak ada teks yang terdeteksi."
                        );
                    } else {
                        tvHasilOCR.setText(hasilTeks);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OcrActivity", "OCR gagal", e);

                    tvHasilOCR.setText(
                            "Gagal mengenali teks. Silakan coba gambar lain."
                    );

                    Toast.makeText(
                            this,
                            "Proses OCR gagal.",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void bacakanHasil() {
        String teks = tvHasilOCR.getText().toString().trim();

        if (teks.isEmpty()
                || teks.equals("Hasil OCR akan muncul di sini.")
                || teks.equals("Teks yang berhasil dibaca akan muncul di sini.")
                || teks.equals("Sedang mengenali teks...")
                || teks.equals("Tidak ada teks yang terdeteksi.")
                || teks.startsWith("Gagal")
                || teks.startsWith("Kamera gagal")
                || teks.startsWith("Izin kamera")) {

            Toast.makeText(
                    this,
                    "Pilih gambar atau foto tulisan terlebih dahulu.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (ttsSiap && textToSpeech != null) {
            textToSpeech.speak(
                    teks,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "hasil_ocr"
            );
        } else {
            Toast.makeText(
                    this,
                    "Fitur suara belum siap. Periksa pengaturan Text-to-Speech.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void tampilkanPesan(String pesan) {
        tvHasilOCR.setText(pesan);
    }

    @Override
    protected void onDestroy() {
        if (recognizer != null) {
            recognizer.close();
        }

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}
