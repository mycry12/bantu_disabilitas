
package com.bantudisabilitas.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MobilityAidActivity extends AppCompatActivity {

    private LinearLayout daftarAlat;
    private EditText pencarian;

    private final int biru = Color.rgb(30, 91, 150);
    private final int hijau = Color.rgb(20, 145, 130);
    private final int gelap = Color.rgb(35, 48, 65);
    private final int latar = Color.rgb(246, 249, 252);

    private final String[][] dataAlat = {
            {"♿", "Kursi Roda",
                    "Kursi roda manual dan elektrik untuk membantu mobilitas."},
            {"🦯", "Tongkat dan Kruk",
                    "Alat bantu berjalan untuk keseimbangan dan menopang tubuh."},
            {"🚶", "Walker",
                    "Alat bantu berjalan untuk memberikan dukungan dan stabilitas."},
            {"↔", "Alat Bantu Transfer",
                    "Informasi papan transfer dan alat bantu berpindah secara aman."},
            {"⌂", "Aksesibilitas Rumah",
                    "Informasi ramp, pegangan tangan, dan penyesuaian rumah."},
            {"🚌", "Transportasi Aksesibel",
                    "Informasi transportasi ramah kursi roda dan fasilitas aksesibel."}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout utama = new LinearLayout(this);
        utama.setOrientation(LinearLayout.VERTICAL);
        utama.setPadding(dp(20), dp(16), dp(20), dp(12));
        utama.setBackgroundColor(latar);

        // Tombol kembali
        TextView kembali = new TextView(this);
        kembali.setText("‹  Kembali");
        kembali.setTextSize(16);
        kembali.setTextColor(biru);
        kembali.setPadding(0, dp(6), 0, dp(18));
        kembali.setContentDescription("Kembali ke halaman sebelumnya");
        kembali.setOnClickListener(v -> finish());
        utama.addView(kembali);

        // Judul halaman
        TextView judul = new TextView(this);
        judul.setText("Mobility Aid");
        judul.setTextSize(27);
        judul.setTypeface(null, Typeface.BOLD);
        judul.setTextColor(gelap);
        utama.addView(judul);

        TextView deskripsi = new TextView(this);
        deskripsi.setText(
                "Temukan alat bantu untuk bergerak lebih aman dan mandiri."
        );
        deskripsi.setTextSize(14);
        deskripsi.setTextColor(Color.DKGRAY);
        deskripsi.setPadding(0, dp(7), 0, dp(18));
        utama.addView(deskripsi);

        // Kartu informasi utama
        LinearLayout banner = new LinearLayout(this);
        banner.setOrientation(LinearLayout.VERTICAL);
        banner.setPadding(dp(16), dp(16), dp(16), dp(16));
        banner.setBackground(buatLatar(biru, 16));

        TextView bannerJudul = new TextView(this);
        bannerJudul.setText("Bergerak Lebih Mandiri");
        bannerJudul.setTextColor(Color.WHITE);
        bannerJudul.setTextSize(18);
        bannerJudul.setTypeface(null, Typeface.BOLD);
        banner.addView(bannerJudul);

        TextView bannerIsi = new TextView(this);
        bannerIsi.setText(
                "Jelajahi alat bantu dan fasilitas untuk mendukung aktivitas harian."
        );
        bannerIsi.setTextColor(Color.WHITE);
        bannerIsi.setTextSize(13);
        bannerIsi.setPadding(0, dp(6), 0, 0);
        banner.addView(bannerIsi);

        LinearLayout.LayoutParams bannerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        bannerParams.bottomMargin = dp(18);
        utama.addView(banner, bannerParams);

        // Kolom pencarian
        pencarian = new EditText(this);
        pencarian.setSingleLine(true);
        pencarian.setTextSize(14);
        pencarian.setHint("Cari alat bantu...");
        pencarian.setContentDescription("Cari kategori alat bantu");
        pencarian.setPadding(dp(14), dp(8), dp(14), dp(8));
        pencarian.setBackground(buatLatar(Color.WHITE, 12));
        pencarian.setCompoundDrawablePadding(dp(8));

        utama.addView(pencarian, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(50)
        ));

        // Judul kategori
        TextView subjudul = new TextView(this);
        subjudul.setText("Kategori Alat Bantu");
        subjudul.setTextSize(19);
        subjudul.setTypeface(null, Typeface.BOLD);
        subjudul.setTextColor(gelap);
        subjudul.setPadding(0, dp(20), 0, dp(12));
        utama.addView(subjudul);

        // Daftar kartu yang dapat digulir
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        daftarAlat = new LinearLayout(this);
        daftarAlat.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(daftarAlat);

        utama.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1
        ));

        setContentView(utama);

        tampilkanAlat("");

        pencarian.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after
            ) {}

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count
            ) {
                tampilkanAlat(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void tampilkanAlat(String kataKunci) {
        daftarAlat.removeAllViews();
        int jumlah = 0;

        String kunci = kataKunci.trim().toLowerCase();

        for (String[] alat : dataAlat) {
            String teksCari = (alat[1] + " " + alat[2])
                    .toLowerCase();

            if (!teksCari.contains(kunci)) {
                continue;
            }

            jumlah++;

            LinearLayout kartu = new LinearLayout(this);
            kartu.setOrientation(LinearLayout.HORIZONTAL);
            kartu.setGravity(Gravity.CENTER_VERTICAL);
            kartu.setPadding(dp(13), dp(14), dp(12), dp(14));
            kartu.setBackground(buatLatar(Color.WHITE, 14));
            kartu.setFocusable(true);
            kartu.setClickable(true);

            LinearLayout.LayoutParams kartuParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            kartuParams.bottomMargin = dp(10);
            daftarAlat.addView(kartu, kartuParams);

            // Ikon kategori
            TextView ikon = new TextView(this);
            ikon.setText(alat[0]);
            ikon.setTextSize(25);
            ikon.setGravity(Gravity.CENTER);
            ikon.setBackground(buatLatar(
                    Color.rgb(225, 244, 241), 12
            ));

            LinearLayout.LayoutParams ikonParams =
                    new LinearLayout.LayoutParams(dp(54), dp(54));
            ikonParams.rightMargin = dp(13);
            kartu.addView(ikon, ikonParams);

            // Nama dan deskripsi
            LinearLayout isi = new LinearLayout(this);
            isi.setOrientation(LinearLayout.VERTICAL);

            kartu.addView(isi, new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
            ));

            TextView nama = new TextView(this);
            nama.setText(alat[1]);
            nama.setTextSize(15);
            nama.setTypeface(null, Typeface.BOLD);
            nama.setTextColor(gelap);
            isi.addView(nama);

            TextView ringkasan = new TextView(this);
            ringkasan.setText(alat[2]);
            ringkasan.setTextSize(13);
            ringkasan.setTextColor(Color.DKGRAY);
            ringkasan.setPadding(0, dp(5), 0, 0);
            isi.addView(ringkasan);

            TextView panah = new TextView(this);
            panah.setText("›");
            panah.setTextSize(26);
            panah.setTextColor(hijau);
            panah.setGravity(Gravity.CENTER);
            kartu.addView(panah);

            kartu.setContentDescription(
                    alat[1] + ". " + alat[2]
                            + " Ketuk untuk melihat informasi."
            );

            kartu.setOnClickListener(v -> tampilkanDetail(alat));
        }

        if (jumlah == 0) {
            TextView kosong = new TextView(this);
            kosong.setText(
                    "Alat tidak ditemukan.\nCoba kata kunci lainnya."
            );
            kosong.setTextSize(14);
            kosong.setTextColor(Color.DKGRAY);
            kosong.setGravity(Gravity.CENTER);
            kosong.setPadding(dp(12), dp(24), dp(12), dp(24));
            daftarAlat.addView(kosong);
        }
    }

    private void tampilkanDetail(String[] alat) {
        new AlertDialog.Builder(this)
                .setTitle(alat[0] + "  " + alat[1])
                .setMessage(
                        alat[2]
                                + "\n\nPilih alat sesuai kebutuhan pengguna. "
                                + "Untuk menentukan jenis dan ukuran yang tepat, "
                                + "mintalah saran tenaga profesional bila diperlukan. "
                                + "Ketersediaan alat dan layanan perlu dikonfirmasi."
                )
                .setPositiveButton("Mengerti", (dialog, which) ->
                        dialog.dismiss()
                )
                .show();
    }

    private GradientDrawable buatLatar(int warna, int radius) {
        GradientDrawable bentuk = new GradientDrawable();
        bentuk.setColor(warna);
        bentuk.setCornerRadius(dp(radius));
        return bentuk;
    }

    private int dp(int nilai) {
        return (int) (nilai * getResources()
                .getDisplayMetrics().density + 0.5f);
    }
}
