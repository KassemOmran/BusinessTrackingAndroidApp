package lb.edu.ul.businesstrackingandroidapp;


import android.os.Bundle;



import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import lb.edu.ul.businesstrackingandroidapp.database.InventoryItem;
import lb.edu.ul.businesstrackingandroidapp.database.Order;
import lb.edu.ul.businesstrackingandroidapp.database.OrderItem;
import lb.edu.ul.businesstrackingandroidapp.database.OrderType;
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

        Toolbar toolbar = binding.toolbar.toolbar;
        setSupportActionBar(binding.toolbar.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle("Place Order");

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
            double totalPrice = 0;
            Order order = new Order();
            order.orderDate=System.currentTimeMillis();
            order.orderType = (isOutgoing)?OrderType.OUTGOING:OrderType.INCOMING;

            List<OrderItem> orderItems= new ArrayList<>();
            orderItems.clear();
            for (InventoryItem item : items) {
                OrderItem i= new OrderItem();
                i.orderId=order.id;
                i.itemId= item.id;
                i.quantity= item.orderQuantity;
                orderItems.add(i);

                if (item.orderQuantity <= 0) continue;

                // ❌ FINAL VALIDATION
                if (isOutgoing && item.orderQuantity > item.quantity) {
                    runOnUiThread(() ->
                            binding.placeOrderBtn.setError("Invalid quantity detected"));
                    return;
                }


                // ✅ APPLY CHANGE
                totalPrice+=item.price * item.orderQuantity;
                if (isOutgoing) {
                    item.quantity -= item.orderQuantity;
                } else {
                    item.quantity += item.orderQuantity;
                }
                MainActivity.db.inventoryItemDao().update(item);

            }
            order.totalPrice = totalPrice;
            MainActivity.db.orderDao().insertOrderWithItems(order,orderItems,MainActivity.db.inventoryItemDao());


            // ✅ SUCCESS → BACK TO DASHBOARD
            runOnUiThread(() -> {
                finish(); // returns to dashboard
            });

        }).start();




    }
}

