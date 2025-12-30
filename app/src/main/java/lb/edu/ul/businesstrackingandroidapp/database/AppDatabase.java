package lb.edu.ul.businesstrackingandroidapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {InventoryItem.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract InventoryItemDao inventoryItemDao();

}
