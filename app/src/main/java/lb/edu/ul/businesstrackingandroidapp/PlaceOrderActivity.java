package lb.edu.ul.businesstrackingandroidapp;


import android.os.Bundle;



import androidx.appcompat.app.AppCompatActivity;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lb.edu.ul.businesstrackingandroidapp.database.InventoryItem;
import lb.edu.ul.businesstrackingandroidapp.databinding.ActivityPlaceOrderBinding;
import lb.edu.ul.businesstrackingandroidapp.PlaceOrderAdapter;

public class PlaceOrderActivity extends AppCompatActivity {

    private ActivityPlaceOrderBinding binding;
    private PlaceOrderAdapter adapter;
    private boolean isOutgoing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = binding.placeOrderRV;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Radio buttons
        binding.rbOutgoing.setChecked(true);

        binding.orderTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            isOutgoing = checkedId == R.id.rbOutgoing;
            if (adapter != null) {
                adapter.setOrderType(isOutgoing);
            }
        });

        MainActivity.db.inventoryItemDao()
                .getAllInventoryItems()
                .observe(this, inventoryItems -> {
                    adapter = new PlaceOrderAdapter(this, inventoryItems);
                    adapter.setOrderType(isOutgoing);
                    recyclerView.setAdapter(adapter);
                });

        // ✅ PLACE ORDER BUTTON
        binding.placeOrderBtn.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        if (adapter == null) return;

        List<InventoryItem> items = adapter.getItems();

        new Thread(() -> {
            for (InventoryItem item : items) {

                if (item.orderQuantity <= 0) continue;

                // ❌ FINAL VALIDATION
                if (isOutgoing && item.orderQuantity > item.quantity) {
                    runOnUiThread(() ->
                            binding.placeOrderBtn.setError("Invalid quantity detected"));
                    return;
                }

                // ✅ APPLY CHANGE
                if (isOutgoing) {
                    item.quantity -= item.orderQuantity;
                } else {
                    item.quantity += item.orderQuantity;
                }

                MainActivity.db.inventoryItemDao().update(item);
            }

            // ✅ SUCCESS → BACK TO DASHBOARD
            runOnUiThread(() -> {
                finish(); // returns to dashboard
            });

        }).start();
    }
}

