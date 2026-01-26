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

        // 🔥 ONE observer ONLY
        MainActivity.db.orderDao().getAllOrders().observe(this, orders -> {

            Collections.sort(orders, (a, b) ->
                    Long.compare(b.orderDate, a.orderDate)
            );

            List<Order> displayOrders = new ArrayList<>();


            switch (filterType) {
                case 1: // INCOMING
                    for (Order o : orders) {
                        if (o.orderType == OrderType.INCOMING) {
                            displayOrders.add(o);
                        }
                    }
                    break;

                case 2: // OUTGOING
                    for (Order o : orders) {
                        if (o.orderType == OrderType.OUTGOING) {
                            displayOrders.add(o);
                        }
                    }
                    break;

                default: // ALL
                    displayOrders.addAll(orders);
            }
            Log.d("DB_CHECK", "Orders count = " + orders.size());

            for (Order o : orders) {
                Log.d("DB_CHECK",
                        "id=" + o.id +
                                ", type=" + o.orderType +
                                ", date=" + o.orderDate
                );
            }
            List<ListItem> finalList = buildListWithDates(displayOrders);
            Log.d("LIST_BUILD", "Final list size=" + finalList.size());
            for (ListItem li : finalList) {
                Log.d("LIST_BUILD", "Item class = " + li.getClass().getSimpleName());
            }
            Log.d("LIST_BUILD", "Final list size=" + finalList.size());
            adapter.submitList(this,finalList);


        });

        // Tabs only change filterType
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterType = tab.getPosition();
                // LiveData automatically re-triggers observer
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

}