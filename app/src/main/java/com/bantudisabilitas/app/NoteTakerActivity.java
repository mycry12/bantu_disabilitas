package com.bantudisabilitas.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NoteTakerActivity extends AppCompatActivity {

    private EditText etJudulCatatan, etIsiCatatan;
    private Button btnSimpanCatatan, btnHapusCatatan;
    private TextView tvDaftarCatatan, btnKembaliNote;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "CylaNotes";
    private static final String KEY_JUDUL = "judul_catatan";
    private static final String KEY_ISI = "isi_catatan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taker);

        etJudulCatatan = findViewById(R.id.etJudulCatatan);
        etIsiCatatan = findViewById(R.id.etIsiCatatan);
        btnSimpanCatatan = findViewById(R.id.btnSimpanCatatan);
        btnHapusCatatan = findViewById(R.id.btnHapusCatatan);
        tvDaftarCatatan = findViewById(R.id.tvDaftarCatatan);
        btnKembaliNote = findViewById(R.id.btnKembaliNote);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Muat catatan tersimpan
        muatCatatan();

        if (btnKembaliNote != null) {
            btnKembaliNote.setOnClickListener(v -> finish());
        }

        if (btnSimpanCatatan != null) {
            btnSimpanCatatan.setOnClickListener(v -> {
                String judul = etJudulCatatan.getText().toString().trim();
                String isi = etIsiCatatan.getText().toString().trim();

                if (judul.isEmpty() && isi.isEmpty()) {
                    Toast.makeText(this, "Judul atau isi catatan tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_JUDUL, judul);
                editor.putString(KEY_ISI, isi);
                editor.apply();

                Toast.makeText(this, "Catatan berhasil disimpan!", Toast.LENGTH_SHORT).show();
                muatCatatan();
            });
        }

        if (btnHapusCatatan != null) {
            btnHapusCatatan.setOnClickListener(v -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(KEY_JUDUL);
                editor.remove(KEY_ISI);
                editor.apply();

                etJudulCatatan.setText("");
                etIsiCatatan.setText("");
                Toast.makeText(this, "Catatan dihapus.", Toast.LENGTH_SHORT).show();
                muatCatatan();
            });
        }
    }

    private void muatCatatan() {
        String judul = sharedPreferences.getString(KEY_JUDUL, "");
        String isi = sharedPreferences.getString(KEY_ISI, "");

        if (judul.isEmpty() && isi.isEmpty()) {
            tvDaftarCatatan.setText("Catatan Tersimpan:\n(Belum ada catatan)");
        } else {
            tvDaftarCatatan.setText("Catatan Tersimpan:\n\nJudul: " + judul + "\n\nIsi:\n" + isi);
        }
    }
}
