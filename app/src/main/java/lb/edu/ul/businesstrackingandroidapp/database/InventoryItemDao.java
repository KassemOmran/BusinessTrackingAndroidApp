package lb.edu.ul.businesstrackingandroidapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface InventoryItemDao {
    @Query("SELECT * FROM inventoryitem")
    LiveData<List<InventoryItem>> getAllInventoryItems();
    @Insert
    void insertAll(InventoryItem inventoryItem);
    @Delete
    void delete(InventoryItem inventoryItem);
    @Query("SELECT * FROM inventoryitem WHERE barcode = :barcode")
    InventoryItem getItemByBarcode(String barcode);

    @Query("UPDATE inventoryitem SET name = :name, description = :description, barcode = :barcode, quantity = :quantity, expiryDate = :expiryDate , image=:imageUri WHERE id=:id")
    void updateInventoryItem(int id, String name, String description, String barcode, int quantity, LocalDate expiryDate,String imageUri);

}
