package lb.edu.ul.businesstrackingandroidapp.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import lb.edu.ul.businesstrackingandroidapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference general;
        general = findPreference("general");
        Preference barcode = findPreference("barcode");
        Preference mycompany = findPreference("mycompany");
        Preference prices = findPreference("prices");

        if (general != null) {
            general.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(requireActivity(), general.class));
                return true;
            });
        }

        if (barcode != null) {
            barcode.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(requireActivity(), Barcode.class));
                return true;
            });
        }

        if (mycompany != null) {
            mycompany.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(requireActivity(), mycompany.class));
                return true;
            });
        }
    }
}





