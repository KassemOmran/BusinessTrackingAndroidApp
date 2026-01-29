package lb.edu.ul.businesstrackingandroidapp.ui.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import lb.edu.ul.businesstrackingandroidapp.MainActivity;
import lb.edu.ul.businesstrackingandroidapp.databinding.FragmentNotificationsBinding;
import lb.edu.ul.businesstrackingandroidapp.database.InventoryItem;

import android.content.SharedPreferences;

import androidx.recyclerview.widget.LinearLayoutManager;


public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        adapter = new NotificationAdapter();
        binding.notificationsRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.notificationsRecyclerView.setAdapter(adapter);

        loadNotifications();

        return binding.getRoot();
    }

    private void loadNotifications() {

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(requireContext());

        int lowStockThreshold =
                Integer.parseInt(prefs.getString("low_stock_threshold", "5"));

        int expiryDays =
                Integer.parseInt(prefs.getString("expiry_warning_days", "7"));

        MainActivity.db.inventoryItemDao()
                .getAllInventoryItems()
                .observe(getViewLifecycleOwner(), items -> {

                    List<NotificationItem> notifications = new ArrayList<>();
                    LocalDate today = LocalDate.now();

                    for (InventoryItem item : items) {

                        // 🔴 LOW STOCK
                        if (item.quantity <= lowStockThreshold) {
                            notifications.add(
                                    new NotificationItem(
                                            NotificationItem.TYPE_LOW_STOCK,
                                            "Low stock",
                                            item.name + " is low (" + item.quantity + " left)"
                                    )
                            );
                        }

                        // ⏰ EXPIRY
                        if (item.expiryDate != null) {

                            long daysLeft =
                                    ChronoUnit.DAYS.between(today, item.expiryDate);

                            if (daysLeft >= 0 && daysLeft <= expiryDays) {
                                notifications.add(
                                        new NotificationItem(
                                                NotificationItem.TYPE_EXPIRY,
                                                "Expiring soon",
                                                item.name + " expires in " + daysLeft + " days"
                                        )
                                );
                            }
                        }

                    }

                    adapter.setNotifications(notifications);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
