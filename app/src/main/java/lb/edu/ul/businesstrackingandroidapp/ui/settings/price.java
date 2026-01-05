package lb.edu.ul.businesstrackingandroidapp.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import lb.edu.ul.businesstrackingandroidapp.R;

public class price extends AppCompatActivity {
    private EditText etUsd;
    private EditText etLbp;
    private TextView tvRate;
    private double currentRate = 89500.0; // Default rate, should be fetched from settings or API
    private boolean isUpdating = false;

    @SuppressLint("MissingInflatedId")
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        etUsd = view.findViewById(R.id.etUsd);
        etLbp = view.findViewById(R.id.etLbp);
        tvRate = view.findViewById(R.id.tvRate);

        setupListeners();

        return view;
    }

    private void setupListeners() {
        etUsd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;
                try {
                    if (s.toString().isEmpty()) {
                        isUpdating = true;
                        etLbp.setText("");
                        isUpdating = false;
                        return;
                    }
                    double usd = Double.parseDouble(s.toString());
                    double lbp = usd * currentRate;

                    isUpdating = true;
                    etLbp.setText(String.format("%.0f", lbp));
                    isUpdating = false;
                } catch (NumberFormatException e) {
                    // Handle invalid input
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etLbp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;
                try {
                    if (s.toString().isEmpty()) {
                        isUpdating = true;
                        etUsd.setText("");
                        isUpdating = false;
                        return;
                    }
                    double lbp = Double.parseDouble(s.toString());
                    double usd = lbp / currentRate;

                    isUpdating = true;
                    etUsd.setText(String.format("%.2f", usd));
                    isUpdating = false;
                } catch (NumberFormatException e) {
                    // Handle invalid input
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}