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

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Spinner setup (list of reports)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.list_Reports,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.listReports.setAdapter(adapter);

        // Show filter dialog when user selects a custom report
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

        Spinner spCategory = dialog.findViewById(R.id.spCategory);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroupDates);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        // Example categories
        String[] categories = {"ALL", "Food", "Drinks", "Bills"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        spCategory.setAdapter(adapter);

        btnOk.setOnClickListener(v -> {
            // Get selected category
            String category = spCategory.getSelectedItem().toString();

            // Get selected date range
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(Analytics.this, "Please select a date range", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton rb = dialog.findViewById(selectedId);
            String dateRange = rb.getText().toString();

            dialog.dismiss();

            // Open fragment and pass data
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
