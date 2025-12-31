package lb.edu.ul.businesstrackingandroidapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

import lb.edu.ul.businesstrackingandroidapp.database.AppDatabase;
import lb.edu.ul.businesstrackingandroidapp.database.InventoryItem;
import lb.edu.ul.businesstrackingandroidapp.databinding.ActivityInventoryBinding;

public class InventoryActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityInventoryBinding binding;
    static AppDatabase db;
    private InventoyItemsAdapter inventoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInventoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        binding.addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(InventoryActivity.this,AddEditInventoryActivity.class);
                i.putExtra("barcode","null");
                startActivity(i);
            }
        });
        RecyclerView rv= findViewById(R.id.itemsRecView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app_db").build();
        db.inventoryItemDao().getAllInventoryItems().observe(this, new Observer<List<InventoryItem>>() {
            @Override
            public void onChanged(List<InventoryItem> inventoryItems) {

                inventoryAdapter=new InventoyItemsAdapter(InventoryActivity.this,inventoryItems,getSupportFragmentManager());
                rv.setAdapter(inventoryAdapter);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inventory_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                inventoryAdapter.getFilter().filter(newText);
                return true;

            }
        });
        return super.onCreateOptionsMenu(menu);
    }






}