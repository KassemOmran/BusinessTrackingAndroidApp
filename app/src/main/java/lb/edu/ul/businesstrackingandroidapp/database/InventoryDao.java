package lb.edu.ul.businesstrackingandroidapp.database;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface InventoryDao {

    @Query("SELECT * FROM InventoryItem WHERE id = :id")
    InventoryItem getItemById(int id);

    @Update
    void update(InventoryItem item);
}

