package lb.edu.ul.businesstrackingandroidapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import lb.edu.ul.businesstrackingandroidapp.databinding.ActivityAnalyticsBinding;

public class Analytics extends AppCompatActivity {

    private ActivityAnalyticsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalyticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar.toolbar;
        setSupportActionBar(binding.toolbar.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle("Analytics");

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.list_Reports,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.listReports.setAdapter(adapter);

        binding.listReports.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = parent.getItemAtPosition(position).toString();
            Toast.makeText(Analytics.this, "You selected: " + selectedItem, Toast.LENGTH_SHORT).show();

            if (selectedItem.equals("Sales by Date")) {
                showFilterDialog();
            }
        });

    }

    private void showFilterDialog() {
        Dialog dialog = new Dialog(Analytics.this);
        dialog.setContentView(R.layout.activity_dialog_filter);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );


        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroupDates);
        Button btnOk = dialog.findViewById(R.id.btnOk);



        btnOk.setOnClickListener(v -> {


            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(Analytics.this, "Please select a date range", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton rb = dialog.findViewById(selectedId);
            String dateRange = rb.getText().toString();

            dialog.dismiss();

            openSalesByDateFragment( dateRange);
        });

        dialog.show();
    }

    private void openSalesByDateFragment( String dateRange) {
        BlankFragment fragment = BlankFragment.newInstance( dateRange);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
