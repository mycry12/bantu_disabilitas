
package com.bantudisabilitas.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LayananActivity extends AppCompatActivity {

    private TextView btnSemua, btnVisual, btnKomunikasi, btnAksesibilitas;
    private TextView tvJudulDaftarLayanan;

    private View cardAsistenVisual, cardDeteksiObjek;
    private View cardKomunikasi, cardAksesibilitas;
    private View cardAlatBantu, cardBantuan, cardPanduan;

    private EditText etCariLayanan;

    private String kategoriAktif = "Semua";
    private String kataKunci = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layanan);

        // KATEGORI
        btnSemua = findViewById(R.id.btnSemua);
        btnVisual = findViewById(R.id.btnVisual);
        btnKomunikasi = findViewById(R.id.btnKomunikasi);
        btnAksesibilitas = findViewById(R.id.btnAksesibilitas);

        tvJudulDaftarLayanan = findViewById(R.id.tvJudulDaftarLayanan);
        etCariLayanan = findViewById(R.id.etCariLayanan);

        // KARTU LAYANAN
        cardAsistenVisual = findViewById(R.id.cardAsistenVisual);
        cardDeteksiObjek = findViewById(R.id.cardDeteksiObjek);
        cardKomunikasi = findViewById(R.id.cardKomunikasi);
        cardAksesibilitas = findViewById(R.id.cardAksesibilitas);
        cardAlatBantu = findViewById(R.id.cardAlatBantu);
        cardBantuan = findViewById(R.id.cardBantuan);
        cardPanduan = findViewById(R.id.cardPanduan);

        // KLIK KATEGORI
        btnSemua.setOnClickListener(v -> pilihKategori("Semua"));
        btnVisual.setOnClickListener(v -> pilihKategori("Visual"));
        btnKomunikasi.setOnClickListener(v -> pilihKategori("Komunikasi"));
        btnAksesibilitas.setOnClickListener(v -> pilihKategori("Aksesibilitas"));

        // PENCARIAN
        etCariLayanan.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kataKunci = s.toString().trim().toLowerCase();
                terapkanFilter();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        // NAVIGASI BAWAH
        pasangNavigasi(R.id.navHome, MainActivity.class);
        pasangNavigasi(R.id.navCatatan, NoteTakerActivity.class);
        pasangNavigasi(R.id.navProfil, ProfilActivity.class);

        // KARTU ASISTEN VISUAL
        pasangNavigasi(R.id.cardAsistenVisual, OcrActivity.class);

        // KARTU DETEKSI OBJEK
        pasangNavigasi(R.id.cardDeteksiObjek, ObjectActivity.class);

        // KARTU KOMUNIKASI
        pasangNavigasi(R.id.cardKomunikasi, CommunicationActivity.class);

        // KARTU AKSESIBILITAS
        pasangNavigasi(R.id.cardAksesibilitas, AccessibilityActivity.class);

        // KARTU ALAT BANTU
        pasangNavigasi(R.id.cardAlatBantu, MobilityAidActivity.class);

        // SATU KARTU BANTUAN DARURAT
        if (cardBantuan != null) {
            cardBantuan.setOnClickListener(v ->
                    BantuanDaruratDialog.tampilkan(this)
            );
        }

        // PANDUAN
        if (cardPanduan != null) {
            cardPanduan.setOnClickListener(v ->
                    Toast.makeText(
                            this,
                            "Panduan akan segera tersedia.",
                            Toast.LENGTH_SHORT
                    ).show()
            );
        }

        // TAMPILAN AWAL
        perbaruiTampilanKategori();
        terapkanFilter();
    }

    // MEMILIH KATEGORI
    private void pilihKategori(String kategori) {
        kategoriAktif = kategori;

        perbaruiTampilanKategori();

        if (tvJudulDaftarLayanan != null) {
            tvJudulDaftarLayanan.setText(
                    kategori.equals("Semua")
                            ? "Semua Layanan"
                            : "Layanan " + kategori
            );
        }

        terapkanFilter();
    }

    // MENGUBAH WARNA KATEGORI
    private void perbaruiTampilanKategori() {
        aturTombolKategori(btnSemua, kategoriAktif.equals("Semua"));
        aturTombolKategori(btnVisual, kategoriAktif.equals("Visual"));
        aturTombolKategori(btnKomunikasi, kategoriAktif.equals("Komunikasi"));
        aturTombolKategori(btnAksesibilitas, kategoriAktif.equals("Aksesibilitas"));
    }

    private void aturTombolKategori(TextView tombol, boolean aktif) {
        if (tombol == null) return;

        if (aktif) {
            tombol.setBackgroundResource(R.drawable.bg_category_active);
            tombol.setTextColor(0xFFFFFFFF);
            tombol.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            tombol.setBackgroundResource(R.drawable.bg_search);
            tombol.setTextColor(0xFF36566C);
            tombol.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    // MENYARING KARTU LAYANAN
    private void terapkanFilter() {

        aturKartu(
                cardAsistenVisual,
                "Visual",
                "asisten visual ocr baca teks dokumen kamera"
        );

        aturKartu(
                cardDeteksiObjek,
                "Visual",
                "deteksi objek ai benda kamera"
        );

        aturKartu(
                cardKomunikasi,
                "Komunikasi",
                "komunikasi teks suara tulisan kalimat"
        );

        aturKartu(
                cardAksesibilitas,
                "Aksesibilitas",
                "aksesibilitas android ukuran teks kontras suara"
        );

        aturKartu(
                cardAlatBantu,
                "Aksesibilitas",
                "mobility aid alat bantu kursi roda tongkat walker"
        );

        aturKartu(
                cardBantuan,
                "Aksesibilitas",
                "bantuan darurat kontak pertolongan ambulans polisi pemadam sar basarnas bnpb 112 119 110 113 115 117"
        );

        aturKartu(
                cardPanduan,
                "Aksesibilitas",
                "panduan disabilitas edukasi hak tips"
        );
    }

    private void aturKartu(View kartu, String kategori, String kata) {

        if (kartu == null) return;

        boolean cocokKategori =
                kategoriAktif.equals("Semua") ||
                        kategoriAktif.equals(kategori);

        boolean cocokPencarian =
                kataKunci.isEmpty() ||
                        kata.contains(kataKunci);

        kartu.setVisibility(
                cocokKategori && cocokPencarian
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    // NAVIGASI
    private void pasangNavigasi(int id, Class<?> tujuan) {
        View tombol = findViewById(id);

        if (tombol != null) {
            tombol.setOnClickListener(v -> {
                Intent intent = new Intent(
                        LayananActivity.this,
                        tujuan
                );

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                );

                startActivity(intent);
            });
        }
    }
}
