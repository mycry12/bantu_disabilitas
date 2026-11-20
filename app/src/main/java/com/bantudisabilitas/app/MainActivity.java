
package com.bantudisabilitas.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etCari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // AREA AMAN LAYAR ANDROID
        View root = findViewById(R.id.main);

        if (root != null) {
            final int paddingLeft = root.getPaddingLeft();
            final int paddingTop = root.getPaddingTop();
            final int paddingRight = root.getPaddingRight();
            final int paddingBottom = root.getPaddingBottom();

            ViewCompat.setOnApplyWindowInsetsListener(root,
                    (view, windowInsets) -> {
                        Insets bars = windowInsets.getInsets(
                                WindowInsetsCompat.Type.systemBars()
                        );

                        view.setPadding(
                                paddingLeft + bars.left,
                                paddingTop + bars.top,
                                paddingRight + bars.right,
                                paddingBottom + bars.bottom
                        );

                        return windowInsets;
                    });
        }

        // PENCARIAN
        etCari = findViewById(R.id.etCari);

        // FITUR UTAMA
        pasangAksi(R.id.btnVisual,
                () -> bukaActivity(OcrActivity.class));

        pasangAksi(R.id.btnDeteksiObjek,
                () -> bukaActivity(ObjectActivity.class));

        pasangAksi(R.id.btnKomunikasi,
                () -> bukaActivity(CommunicationActivity.class));

        pasangAksi(R.id.btnAksesibilitas,
                () -> bukaActivity(AccessibilityActivity.class));

        pasangAksi(R.id.btnMobilityAid,
                () -> bukaActivity(MobilityAidActivity.class));

        // BANTUAN DARURAT
        pasangAksi(R.id.btnBantuan,
                () -> BantuanDaruratDialog.tampilkan(this));

        // NAVIGASI BAWAH
        pasangAksi(R.id.navHome, () -> {
            // Sudah berada di Beranda
        });

        pasangAksi(R.id.navLayanan,
                this::bukaLayanan);

        pasangAksi(R.id.navCatatan,
                () -> bukaActivity(NoteTakerActivity.class));

        pasangAksi(R.id.navProfil,
                this::bukaProfil);

        // PENCARIAN FITUR
        if (etCari != null) {
            etCari.setOnEditorActionListener((v, actionId, event) -> {
                cariFitur(etCari.getText().toString().trim());
                return true;
            });
        }
    }

    private void pasangAksi(int id, Runnable aksi) {
        View tampilan = findViewById(id);

        if (tampilan != null) {
            tampilan.setOnClickListener(v -> aksi.run());
        }
    }

    private void bukaActivity(Class<?> tujuan) {
        startActivity(new Intent(MainActivity.this, tujuan));
    }

    private void bukaLayanan() {
        bukaActivity(LayananActivity.class);
    }

    private void bukaProfil() {
        bukaActivity(ProfilActivity.class);
    }

    private void cariFitur(String kata) {
        if (kata.isEmpty()) {
            Toast.makeText(
                    this,
                    "Masukkan nama fitur yang ingin dicari.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String q = kata.toLowerCase(Locale.ROOT);

        if (q.contains("mobilitas")
                || q.contains("kursi roda")
                || q.contains("tongkat")
                || q.contains("kruk")
                || q.contains("walker")
                || q.contains("alat bantu jalan")
                || q.contains("alat bantu transfer")
                || q.contains("ramp")
                || q.contains("transportasi ramah disabilitas")) {

            bukaActivity(MobilityAidActivity.class);

        } else if (q.contains("komunikasi")
                || q.contains("suara")
                || q.contains("bicara")
                || q.contains("ucapan")) {

            bukaActivity(CommunicationActivity.class);

        } else if (q.contains("aksesibilitas")
                || q.contains("talkback")
                || q.contains("ukuran teks")
                || q.contains("kontras")) {

            bukaActivity(AccessibilityActivity.class);

        } else if (q.contains("deteksi")
                || q.contains("objek")
                || q.contains("benda")) {

            bukaActivity(ObjectActivity.class);

        } else if (q.contains("darurat")
                || q.contains("bantuan")
                || q.contains("ambulans")
                || q.contains("polisi")
                || q.contains("pemadam")
                || q.contains("basarnas")
                || q.contains("bnpb")) {

            BantuanDaruratDialog.tampilkan(this);

        } else if (q.contains("visual")
                || q.contains("kamera")
                || q.contains("ocr")
                || q.contains("baca teks")
                || q.contains("foto")
                || q.contains("dokumen")) {

            bukaActivity(OcrActivity.class);

        } else if (q.contains("catatan")
                || q.contains("rekam")
                || q.contains("ringkasan")
                || q.equals("ai")) {

            Toast.makeText(
                    this,
                    "Fitur tersebut belum tersedia.",
                    Toast.LENGTH_SHORT
            ).show();

        } else {
            Toast.makeText(
                    this,
                    "Fitur belum ditemukan. Coba kata lain.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
