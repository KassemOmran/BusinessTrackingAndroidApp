package lb.edu.ul.businesstrackingandroidapp.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lb.edu.ul.businesstrackingandroidapp.MainActivity;
import lb.edu.ul.businesstrackingandroidapp.R;
import lb.edu.ul.businesstrackingandroidapp.database.InventoryItem;
import lb.edu.ul.businesstrackingandroidapp.database.OrderType;
import lb.edu.ul.businesstrackingandroidapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        loadMonthlyRevenue();
        loadNotificationAlarm();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.checkNotificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
                if (navHostFragment!=null){
                    navHostFragment.getNavController().navigate(R.id.navigation_notifications);
                }
            }
        });
    }

    // 🔹 Monthly revenue
    private void loadMonthlyRevenue() {
        long start = startOfCurrentMonth();
        long end = endOfCurrentMonth();

        // create a one-off executor
        Executors.newSingleThreadExecutor().execute(() -> {

            Double result = MainActivity.db.orderDao()
                    .getTotalSalesByDateRange(OrderType.OUTGOING, start, end);

            final double total = (result == null) ? 0.0 : result;

            requireActivity().runOnUiThread(() ->
                    binding.homeTextView.setText(
                            "📊 Monthly Revenue\n\n$ " + total
                    )
            );
        });
    }


    // 🔹 Notification alarm
    private void loadNotificationAlarm() {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(requireContext());

        int lowStockThreshold =
                Integer.parseInt(prefs.getString("low_stock_threshold", "5"));

        int expiryDays =
                Integer.parseInt(prefs.getString("expiry_warning_days", "7"));

        MainActivity.db.inventoryItemDao()
                .getAllInventoryItems()
                .observe(getViewLifecycleOwner(), items -> {

                    boolean hasAlert = false;
                    LocalDate today = LocalDate.now();

                    for (InventoryItem item : items) {

                        if (item.quantity <= lowStockThreshold) {
                            hasAlert = true;
                            break;
                        }

                        if (item.expiryDate != null) {
                            long days =
                                    ChronoUnit.DAYS.between(today, item.expiryDate);
                            if (days >= 0 && days <= expiryDays) {
                                hasAlert = true;
                                break;
                            }
                        }
                    }

                    binding.notificationsLayout.setVisibility(
                            hasAlert ? View.VISIBLE : View.GONE
                    );
                });
    }

    // 🔹 Date helpers
    private long startOfCurrentMonth() {
        return LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    private long endOfCurrentMonth() {
        return LocalDate.now()
                .withDayOfMonth(LocalDate.now().lengthOfMonth())
                .atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }
}
