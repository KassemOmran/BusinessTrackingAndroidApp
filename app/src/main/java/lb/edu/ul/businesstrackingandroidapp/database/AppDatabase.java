package lb.edu.ul.businesstrackingandroidapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {InventoryItem.class},version = 1,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract InventoryItemDao inventoryItemDao();

}
