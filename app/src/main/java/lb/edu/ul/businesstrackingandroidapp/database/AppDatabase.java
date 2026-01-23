package lb.edu.ul.businesstrackingandroidapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;



@Database(
        entities = {
                InventoryItem.class,
                Order.class,
                OrderItem.class
        },
        version = 2
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract InventoryItemDao inventoryItemDao();
    public abstract OrderDao orderDao();

}

