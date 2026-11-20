
package com.bantudisabilitas.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AccessibilityActivity extends AppCompatActivity {

    private TextView tvContohTeks;
    private TextView tvUkuran;
    private TextView tvJudul;
    private TextView tvDeskripsi;
    private TextView btnKembali;

    private Button btnTalkBack;
    private Button btnBaca;

    private CheckBox cbKontras;
    private SeekBar seekUkuran;
    private LinearLayout layoutAkses;

    private TextToSpeech textToSpeech;
    private boolean ttsSiap = false;

    private final int warnaBiru = Color.rgb(20, 45, 78);
    private final int warnaLatar = Color.rgb(244, 248, 252);
    private final int warnaKartu = Color.rgb(234, 245, 245);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);

        // Menghubungkan komponen XML
        layoutAkses = findViewById(R.id.layoutAkses);
        tvContohTeks = findViewById(R.id.tvContohTeks);
        tvUkuran = findViewById(R.id.tvUkuran);
        tvJudul = findViewById(R.id.tvJudulAkses);
        tvDeskripsi = findViewById(R.id.tvDeskripsiAkses);

        btnKembali = findViewById(R.id.btnKembali);
        btnTalkBack = findViewById(R.id.btnTalkBack);
        btnBaca = findViewById(R.id.btnBaca);

        cbKontras = findViewById(R.id.cbKontras);
        seekUkuran = findViewById(R.id.seekUkuran);

        // Kembali ke halaman sebelumnya
        btnKembali.setOnClickListener(v -> finish());

        // Membuka pengaturan TalkBack Android
        btnTalkBack.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(
                        Settings.ACTION_ACCESSIBILITY_SETTINGS
                );
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(
                        this,
                        "Pengaturan aksesibilitas tidak dapat dibuka.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        // Mengatur ukuran contoh teks
        seekUkuran.setMax(20);
        seekUkuran.setProgress(4);
        ubahUkuranTeks(4);

        seekUkuran.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser
                    ) {
                        ubahUkuranTeks(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        // Mengaktifkan atau menonaktifkan kontras tinggi
        cbKontras.setOnCheckedChangeListener((buttonView, checked) -> {
            if (checked) {
                layoutAkses.setBackgroundColor(Color.BLACK);

                ubahWarnaTeks(layoutAkses, Color.WHITE);

                tvContohTeks.setBackgroundColor(Color.BLACK);
                tvContohTeks.setTextColor(Color.WHITE);

                btnBaca.setTextColor(Color.BLACK);
                btnBaca.setBackgroundTintList(
                        ColorStateList.valueOf(Color.WHITE)
                );

                btnTalkBack.setTextColor(Color.BLACK);
                btnTalkBack.setBackgroundTintList(
                        ColorStateList.valueOf(Color.WHITE)
                );
            } else {
                layoutAkses.setBackgroundColor(warnaLatar);

                ubahWarnaTeks(layoutAkses, warnaBiru);

                tvContohTeks.setBackgroundColor(warnaKartu);
                tvContohTeks.setTextColor(warnaBiru);

                btnBaca.setTextColor(Color.WHITE);
                btnBaca.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.rgb(33, 110, 130)
                        )
                );

                btnTalkBack.setTextColor(Color.WHITE);
                btnTalkBack.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.rgb(33, 110, 130)
                        )
                );
            }
        });

        // Menyiapkan pembaca suara Bahasa Indonesia
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int hasil = textToSpeech.setLanguage(
                        new Locale("id", "ID")
                );

                ttsSiap = hasil != TextToSpeech.LANG_MISSING_DATA
                        && hasil != TextToSpeech.LANG_NOT_SUPPORTED;

                if (!ttsSiap) {
                    Toast.makeText(
                            this,
                            "Bahasa Indonesia belum tersedia di perangkat.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            } else {
                ttsSiap = false;
            }
        });

        // Membacakan contoh teks
        btnBaca.setOnClickListener(v -> {
            if (ttsSiap && textToSpeech != null) {
                textToSpeech.speak(
                        tvContohTeks.getText().toString(),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "cyla_aksesibilitas"
                );
            } else {
                Toast.makeText(
                        this,
                        "Suara belum siap. Periksa pengaturan Text-to-Speech.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Label untuk membantu pengguna TalkBack
        btnKembali.setContentDescription(
                "Kembali ke halaman sebelumnya"
        );

        btnTalkBack.setContentDescription(
                "Buka pengaturan aksesibilitas Android untuk mengaktifkan TalkBack"
        );

        btnBaca.setContentDescription(
                "Bacakan contoh teks menggunakan suara"
        );

        seekUkuran.setContentDescription(
                "Geser untuk mengatur ukuran contoh teks"
        );
    }

    private void ubahUkuranTeks(int progress) {
        int ukuran = 12 + progress;

        tvContohTeks.setTextSize(ukuran);
        tvUkuran.setText("Ukuran teks: " + ukuran + " sp");
    }

    private void ubahWarnaTeks(View view, int warna) {
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(warna);
        }

        if (view instanceof CheckBox) {
            ((CheckBox) view).setTextColor(warna);
        }

        if (view instanceof LinearLayout) {
            LinearLayout grup = (LinearLayout) view;

            for (int i = 0; i < grup.getChildCount(); i++) {
                ubahWarnaTeks(grup.getChildAt(i), warna);
            }
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
