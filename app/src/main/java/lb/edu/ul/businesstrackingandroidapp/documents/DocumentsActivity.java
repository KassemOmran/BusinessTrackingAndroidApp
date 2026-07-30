package lb.edu.ul.businesstrackingandroidapp.documents;

import static lb.edu.ul.businesstrackingandroidapp.documents.OrderListBuilder.buildListWithDates;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lb.edu.ul.businesstrackingandroidapp.MainActivity;
import lb.edu.ul.businesstrackingandroidapp.database.Order;
import lb.edu.ul.businesstrackingandroidapp.database.OrderType;
import lb.edu.ul.businesstrackingandroidapp.databinding.ActivityDocumentsBinding;

public class DocumentsActivity extends AppCompatActivity {

    private ActivityDocumentsBinding binding;
    int filterType = 0;
    List<Order> filteredOrders = new ArrayList<>();
    private OrdersAdapter adapter;
    private List<Order> allOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDocumentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar.toolbar;
        setSupportActionBar(binding.toolbar.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle("Documents");

        adapter = new OrdersAdapter();
        binding.documentsRecView.setLayoutManager(new LinearLayoutManager(this));
        binding.documentsRecView.setAdapter(adapter);

        TabLayout tabLayout = binding.tabs;
        tabLayout.addTab(tabLayout.newTab().setText("ALL"));
        tabLayout.addTab(tabLayout.newTab().setText("INCOMING"));
        tabLayout.addTab(tabLayout.newTab().setText("OUTGOING"));

        MainActivity.db.orderDao().getAllOrders().observe(this, orders -> {
            allOrders = orders;
            refreshList();
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterType = tab.getPosition();
                refreshList();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

    }
    private void refreshList() {
                if (allOrders == null) return;

                List<Order> displayOrders = new ArrayList<>();

                Collections.sort(allOrders, (a, b) ->
                        Long.compare(b.orderDate, a.orderDate)
                );

                switch (filterType) {
                    case 1: // INCOMING
                        for (Order o : allOrders) {
                            if (o.orderType == OrderType.INCOMING) {
                                displayOrders.add(o);
                            }
                        }
                        break;

                    case 2: // OUTGOING
                        for (Order o : allOrders) {
                            if (o.orderType == OrderType.OUTGOING) {
                                displayOrders.add(o);
                            }
                        }
                        break;

                    default: // ALL
                        displayOrders.addAll(allOrders);
                }

                List<ListItem> finalList = buildListWithDates(displayOrders);
                adapter.submitList(this, finalList);
            }
    }

