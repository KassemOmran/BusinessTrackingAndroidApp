package lb.edu.ul.businesstrackingandroidapp.ui.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

import lb.edu.ul.businesstrackingandroidapp.MainActivity;
import lb.edu.ul.businesstrackingandroidapp.R;

public class general extends AppCompatActivity {

    Spinner spinner;
    TextView password;
    Switch enter;

    public final String[] languages = {"select language", "english", "arabic"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_general);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ===== Spinner (Language) =====
        spinner = findViewById(R.id.spinnerLanguage);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        languages);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedlang = parent.getItemAtPosition(position).toString();

                if (selectedlang.equals("english")) {
                    setlocal(general.this, "en");
                    finish();
                    startActivity(getIntent());

                } else if (selectedlang.equals("arabic")) {
                    setlocal(general.this, "ar");
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // ===== Password / Enter =====
        password = findViewById(R.id.Password);
        enter = findViewById(R.id.passcode);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = password.getText().toString();

                if (!input.equals("")) {

                    if (input.equals(getCurrentpassword())) {
                        startActivity(new Intent(general.this, MainActivity.class));
                        finish();
                    }

                } else {
                    Toast.makeText(general.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ===== Locale Method =====
    public void setlocal(general activity, String langcode) {

        Locale locale = new Locale(langcode);
        Locale.setDefault(locale);

        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    // ===== Password Generator =====
    private String getCurrentpassword() {
        Calendar calendar = Calendar.getInstance();
        int curHour12 = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        return curHour12 + "" + minute;
    }
}
