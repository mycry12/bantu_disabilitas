
package com.bantudisabilitas.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    LinearLayout menuAksesibilitas, menuUkuranTeks, menuSuara;
    LinearLayout menuNotifikasi, menuBahasa;
    LinearLayout menuTentang, menuPanduan, menuPrivasi;

    View navHome, navLayanan, navCatatan, navProfil;
    Button btnKeluar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // MENU PENGATURAN
        menuAksesibilitas = findViewById(R.id.menuAksesibilitas);
        menuUkuranTeks = findViewById(R.id.menuUkuranTeks);
        menuSuara = findViewById(R.id.menuSuara);
        menuNotifikasi = findViewById(R.id.menuNotifikasi);
        menuBahasa = findViewById(R.id.menuBahasa);
        menuTentang = findViewById(R.id.menuTentang);
        menuPanduan = findViewById(R.id.menuPanduan);
        menuPrivasi = findViewById(R.id.menuPrivasi);

        btnKeluar = findViewById(R.id.btnKeluar);

        // NAVIGASI BAWAH
        navHome = findViewById(R.id.navHome);
        navLayanan = findViewById(R.id.navLayanan);
        navCatatan = findViewById(R.id.navCatatan);
        navProfil = findViewById(R.id.navProfil);

        if (navHome != null) {
            navHome.setOnClickListener(v ->
                    bukaHalaman(MainActivity.class));
        }

        if (navLayanan != null) {
            navLayanan.setOnClickListener(v ->
                    bukaHalaman(LayananActivity.class));
        }

        if (navCatatan != null) {
            navCatatan.setOnClickListener(v ->
                    bukaHalaman(NoteTakerActivity.class));
        }

        // Sudah berada di halaman Profil
        if (navProfil != null) {
            navProfil.setOnClickListener(v -> {});
        }

        // MENU PENGATURAN
        pasangPesan(menuAksesibilitas, "Pengaturan Aksesibilitas");
        pasangPesan(menuUkuranTeks, "Pengaturan Ukuran Teks");
        pasangPesan(menuSuara, "Pengaturan Suara");
        pasangPesan(menuNotifikasi, "Pengaturan Notifikasi");
        pasangPesan(menuBahasa, "Pengaturan Bahasa");

        pasangPesan(menuTentang,
                "Cyla Care adalah aplikasi pendamping aksesibilitas untuk membantu aktivitas sehari-hari.");

        pasangPesan(menuPanduan,
                "Panduan penggunaan Cyla Care");

        pasangPesan(menuPrivasi,
                "Informasi privasi aplikasi Cyla Care");

        // KELUAR
        if (btnKeluar != null) {
            btnKeluar.setOnClickListener(v ->
                    konfirmasiKeluar());
        }
    }

    // NAVIGASI ANTAR HALAMAN
    private void bukaHalaman(Class<?> tujuan) {
        Intent intent = new Intent(ProfilActivity.this, tujuan);
        startActivity(intent);
    }

    // PESAN MENU
    private void pasangPesan(LinearLayout menu, String pesan) {
        if (menu != null) {
            menu.setOnClickListener(v ->
                    Toast.makeText(
                            this,
                            pesan,
                            Toast.LENGTH_SHORT
                    ).show());
        }
    }

    // KONFIRMASI KELUAR
    private void konfirmasiKeluar() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar Aplikasi")
                .setMessage("Apakah kamu yakin ingin kembali ke halaman login?")
                .setPositiveButton("Ya",
                        (dialog, which) -> kembaliKeLogin())
                .setNegativeButton("Batal", null)
                .show();
    }

    // KEMBALI KE LOGIN
    private void kembaliKeLogin() {
        Intent intent = new Intent(
                ProfilActivity.this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
