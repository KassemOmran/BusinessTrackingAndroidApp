package lb.edu.ul.businesstrackingandroidapp.documents;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lb.edu.ul.businesstrackingandroidapp.MainActivity;
import lb.edu.ul.businesstrackingandroidapp.database.DateUtils;
import lb.edu.ul.businesstrackingandroidapp.database.Order;
import lb.edu.ul.businesstrackingandroidapp.database.OrderItem;
import lb.edu.ul.businesstrackingandroidapp.databinding.ActivityOrderDetailsBinding;

import lb.edu.ul.businesstrackingandroidapp.R;

public class OrderDetailsActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityOrderDetailsBinding binding;
    private TextView orderTitle, orderDate, orderSummary;
    private RecyclerView recyclerView;
    private OrderItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar.toolbar;
        setSupportActionBar(binding.toolbar.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        orderDate = findViewById(R.id.orderDate);
        orderSummary = findViewById(R.id.orderSummary);
        recyclerView = findViewById(R.id.orderItemsRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderItemsAdapter();
        recyclerView.setAdapter(adapter);

        int orderId = getIntent().getIntExtra("ORDER_ID",0);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Fetch order in background
            Order order = MainActivity.db.orderDao().getOrderById(orderId);
            List<OrderItem> items = MainActivity.db.orderDao().getAllOrderItemForOrder(order.id);

            // Update UI on main thread
            runOnUiThread(() -> bindOrder(order,items));
        });

    }

    private void bindOrder(Order order, List<OrderItem> items) {
        orderDate.setText(DateUtils.formatDate(order.orderDate));
        orderSummary.setText(items.size() + " items");

        adapter.submitList(items);
        getSupportActionBar().setTitle(order.orderType + " №" + order.id);
    }







}