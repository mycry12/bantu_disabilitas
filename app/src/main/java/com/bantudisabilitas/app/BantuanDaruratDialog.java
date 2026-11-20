
package com.bantudisabilitas.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class BantuanDaruratDialog {

    public static void tampilkan(Context context) {

        View tampilan = android.view.LayoutInflater.from(context)
                .inflate(R.layout.dialog_bantuan_darurat, null);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("🆘 Bantuan Darurat")
                .setView(tampilan)
                .setNegativeButton("Tutup", null)
                .create();

        int[] idKartu = {
                R.id.daruratUmum,
                R.id.daruratAmbulans,
                R.id.daruratPolisi,
                R.id.daruratPemadam,
                R.id.daruratSAR,
                R.id.daruratBNPB
        };

        String[] nomor = {
                "112",
                "119",
                "110",
                "113",
                "115",
                "117"
        };

        dialog.show();

        for (int i = 0; i < idKartu.length; i++) {

            final String nomorTujuan = nomor[i];

            View kartu = tampilan.findViewById(idKartu[i]);

            if (kartu != null) {
                kartu.setOnClickListener(v ->
                        bukaTelepon(context, nomorTujuan, dialog)
                );
            }
        }
    }

    public static void bukaTelepon(Context context, String nomor) {
        bukaTelepon(context, nomor, null);
    }

    private static void bukaTelepon(
            Context context,
            String nomor,
            AlertDialog dialog
    ) {
        try {
            Intent intent = new Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + nomor)
            );

            context.startActivity(intent);

            if (dialog != null) {
                dialog.dismiss();
            }

        } catch (Exception e) {
            Toast.makeText(
                    context,
                    "Aplikasi telepon tidak tersedia.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
