
package com.bantudisabilitas.app;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FeatureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String halaman = getIntent().getStringExtra("halaman");

        if (halaman == null) {
            halaman = "Cyla Care";
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundColor(0xFFF2F8FC);

        TextView judul = new TextView(this);
        judul.setText(halaman);
        judul.setTextSize(26);
        judul.setTextColor(0xFF0878D1);
        judul.setGravity(Gravity.CENTER);

        TextView keterangan = new TextView(this);
        keterangan.setText(
                "Selamat datang di " + halaman
                        + ".\n\nFitur ini akan kita kembangkan selanjutnya."
        );
        keterangan.setTextSize(16);
        keterangan.setTextColor(0xFF17324D);
        keterangan.setGravity(Gravity.CENTER);
        keterangan.setPadding(0, 24, 0, 0);

        layout.addView(judul);
        layout.addView(keterangan);

        setContentView(layout);
    }
}
