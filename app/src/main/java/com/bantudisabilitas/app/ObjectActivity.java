
package com.bantudisabilitas.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class ObjectActivity extends AppCompatActivity {

    private static final String MODEL_NAME = "efficientdet-lite0.tflite";

    private TextView tvHasilObjek;
    private ImageView ivPratinjauObjek;
    private ObjectDetector detector;
    private TextToSpeech tts;

    private ActivityResultLauncher<Void> kameraLauncher;
    private ActivityResultLauncher<String> galeriLauncher;
    private ActivityResultLauncher<String> izinKameraLauncher;

    private String hasilTerakhir = "";
    private boolean ttsSiap = false;
    private boolean sedangMemproses = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);

        // Tombol kembali
        TextView btnKembali = findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(v -> finish());

        tvHasilObjek = findViewById(R.id.tvHasilObjek);
        ivPratinjauObjek = findViewById(R.id.ivPratinjauObjek);

        Button btnKamera = findViewById(R.id.btnKameraObjek);
        Button btnGaleri = findViewById(R.id.btnGaleriObjek);
        Button btnBaca = findViewById(R.id.btnBacaObjek);

        kameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        ivPratinjauObjek.setImageBitmap(bitmap);
                        deteksiObjek(bitmap);
                    } else {
                        tampilkanPesan(
                                "Foto tidak diambil. Coba buka kamera lagi."
                        );
                    }
                }
        );

        galeriLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        bukaGambarGaleri(uri);
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

        inisialisasiDetektor();

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS && tts != null) {
                int bahasa = tts.setLanguage(new Locale("id", "ID"));

                ttsSiap = bahasa != TextToSpeech.LANG_MISSING_DATA
                        && bahasa != TextToSpeech.LANG_NOT_SUPPORTED;
            }
        });

        btnKamera.setOnClickListener(v -> {
            if (!sedangMemproses) {
                periksaIzinKamera();
            }
        });

        btnGaleri.setOnClickListener(v -> {
            if (!sedangMemproses) {
                galeriLauncher.launch("image/*");
            }
        });

        btnBaca.setOnClickListener(v -> bacaHasil());
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
            tampilkanPesan("Tidak dapat membuka kamera: " + e.getMessage());
            android.util.Log.e("ObjectActivity", "Gagal membuka kamera", e);
        }
    }

    private void inisialisasiDetektor() {
        try {
            BaseOptions baseOptions = BaseOptions.builder()
                    .setNumThreads(4)
                    .build();

            ObjectDetector.ObjectDetectorOptions options =
                    ObjectDetector.ObjectDetectorOptions.builder()
                            .setBaseOptions(baseOptions)
                            .setScoreThreshold(0.35f)
                            .setMaxResults(10)
                            .build();

            detector = ObjectDetector.createFromFileAndOptions(
                    this, MODEL_NAME, options
            );

            tvHasilObjek.setText(
                    "Detektor siap. Ambil foto atau pilih gambar."
            );

        } catch (Exception e) {
            detector = null;
            tvHasilObjek.setText(
                    "Model AI gagal dimuat.\n" +
                            e.getClass().getSimpleName() + ": " + e.getMessage()
            );
            android.util.Log.e("ObjectActivity", "Gagal memuat model AI", e);
        }
    }

    private void bukaGambarGaleri(Uri uri) {
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri)) {

            if (inputStream == null) {
                tampilkanPesan("Gambar tidak dapat dibuka.");
                return;
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap == null) {
                tampilkanPesan("Format gambar tidak didukung.");
                return;
            }

            ivPratinjauObjek.setImageBitmap(bitmap);
            deteksiObjek(bitmap);

        } catch (Exception e) {
            tampilkanPesan("Gagal membuka gambar: " + e.getMessage());
        }
    }

    private void deteksiObjek(Bitmap bitmap) {
        if (detector == null) {
            tampilkanPesan(
                    "Detektor belum siap. Periksa pesan error model AI."
            );
            return;
        }

        if (sedangMemproses) return;

        sedangMemproses = true;
        hasilTerakhir = "";
        tvHasilObjek.setText("Sedang mengenali objek...");

        new Thread(() -> {
            try {
                TensorImage tensorImage = TensorImage.fromBitmap(bitmap);
                List<Detection> hasil = detector.detect(tensorImage);

                runOnUiThread(() -> {
                    sedangMemproses = false;
                    tampilkanHasil(hasil);
                });

            } catch (Exception e) {
                android.util.Log.e(
                        "ObjectActivity", "Proses deteksi gagal", e
                );

                runOnUiThread(() -> {
                    sedangMemproses = false;
                    tampilkanPesan("Deteksi gagal: " + e.getMessage());
                });
            }
        }).start();
    }

    private void tampilkanHasil(List<Detection> detections) {
        if (detections == null || detections.isEmpty()) {
            tampilkanPesan(
                    "Tidak ada objek yang dikenali.\n" +
                            "Coba gambar lain dengan pencahayaan cukup."
            );
            return;
        }

        StringBuilder hasil = new StringBuilder();
        hasil.append("HASIL DETEKSI OBJEK\n\n");
        hasil.append("Jumlah objek: ").append(detections.size()).append("\n\n");

        int nomor = 1;

        for (Detection detection : detections) {
            List<Category> categories = detection.getCategories();

            hasil.append(nomor++).append(". ");

            if (categories == null || categories.isEmpty()) {
                hasil.append("Objek tidak diketahui\n\n");
                continue;
            }

            Category category = categories.get(0);

            String labelIndonesia = terjemahkanLabel(category.getLabel());
            int persen = Math.round(category.getScore() * 100);

            hasil.append(labelIndonesia)
                    .append("\nKeyakinan model: ")
                    .append(persen)
                    .append("%");

            RectF kotak = detection.getBoundingBox();

            hasil.append("\nPosisi gambar: kiri ")
                    .append(Math.round(kotak.left))
                    .append(", atas ").append(Math.round(kotak.top))
                    .append(", kanan ").append(Math.round(kotak.right))
                    .append(", bawah ").append(Math.round(kotak.bottom))
                    .append("\n\n");
        }

        hasilTerakhir = hasil.toString().trim();
        tvHasilObjek.setText(hasilTerakhir);
    }

    private String terjemahkanLabel(String label) {
        if (label == null) return "Objek tidak diketahui";

        switch (label.toLowerCase(Locale.ROOT)) {
            case "person": return "Orang";
            case "bicycle": return "Sepeda";
            case "car": return "Mobil";
            case "motorcycle": return "Sepeda motor";
            case "airplane": return "Pesawat";
            case "bus": return "Bus";
            case "train": return "Kereta";
            case "truck": return "Truk";
            case "boat": return "Perahu";
            case "traffic light": return "Lampu lalu lintas";
            case "bird": return "Burung";
            case "cat": return "Kucing";
            case "dog": return "Anjing";
            case "backpack": return "Tas punggung";
            case "umbrella": return "Payung";
            case "bottle": return "Botol";
            case "cup": return "Cangkir";
            case "fork": return "Garpu";
            case "knife": return "Pisau";
            case "spoon": return "Sendok";
            case "banana": return "Pisang";
            case "apple": return "Apel";
            case "orange": return "Jeruk";
            case "chair": return "Kursi";
            case "couch": return "Sofa";
            case "bed": return "Tempat tidur";
            case "dining table": return "Meja makan";
            case "tv": return "Televisi";
            case "laptop": return "Laptop";
            case "mouse": return "Mouse komputer";
            case "keyboard": return "Keyboard";
            case "cell phone": return "Ponsel";
            case "book": return "Buku";
            case "clock": return "Jam";
            case "scissors": return "Gunting";
            case "toothbrush": return "Sikat gigi";
            default: return label;
        }
    }

    private void bacaHasil() {
        if (hasilTerakhir.isEmpty()) {
            Toast.makeText(
                    this,
                    "Deteksi objek terlebih dahulu.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (tts != null && ttsSiap) {
            tts.speak(
                    hasilTerakhir,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "hasil_objek"
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
        hasilTerakhir = "";
        tvHasilObjek.setText(pesan);
    }

    @Override
    protected void onDestroy() {
        if (detector != null) {
            detector.close();
        }

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }
}
