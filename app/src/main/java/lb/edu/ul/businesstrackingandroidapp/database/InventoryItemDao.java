package lb.edu.ul.businesstrackingandroidapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InventoryItemDao {
    @Query("SELECT * FROM inventoryitem")
    LiveData<List<InventoryItem>> getAllInventoryItems();
    @Insert
    void insertAll(InventoryItem inventoryItem);
    @Delete
    void delete(InventoryItem inventoryItem);

}
