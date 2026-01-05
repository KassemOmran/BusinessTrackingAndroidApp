package lb.edu.ul.businesstrackingandroidapp.ui.settings;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import lb.edu.ul.businesstrackingandroidapp.R;

public class Barcode extends AppCompatActivity {

    private Switch switchBeep, switchVibrate;
    private boolean isBeepEnabled = true;
    private boolean isVibrateEnabled = true;

    private ToneGenerator toneGenerator;
    private Vibrator vibrator;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barcode);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.barcode), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });

        // Initialize views
        switchBeep = findViewById(R.id.switchBeep);
        switchVibrate = findViewById(R.id.switchvibrate);

        // Tone generator
        toneGenerator = new ToneGenerator(
                AudioManager.STREAM_MUSIC,
                100
        );

        // Vibrator
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Switch listeners
        switchBeep.setOnCheckedChangeListener((buttonView, isChecked) ->
                isBeepEnabled = isChecked
        );

        switchVibrate.setOnCheckedChangeListener((buttonView, isChecked) ->
                isVibrateEnabled = isChecked
        );
    }

    // 🔔📳 CALL this when barcode is detected
    private void onBarcodeDetected(String barcodeValue) {

        // Beep
        if (isBeepEnabled && toneGenerator != null) {
            toneGenerator.startTone(
                    ToneGenerator.TONE_PROP_BEEP,
                    150
            );
        }

        // Vibrate
        if (isVibrateEnabled && vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                        VibrationEffect.createOneShot(
                                150,
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                );
            } else {
                vibrator.vibrate(150);
            }
        }

        // TODO: handle barcode value
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (toneGenerator != null) {
            toneGenerator.release();
        }
    }
}

