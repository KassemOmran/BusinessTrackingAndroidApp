package lb.edu.ul.businesstrackingandroidapp.database;

import android.media.Image;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class InventoryItem {
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "name")
    String name;
    @ColumnInfo(name = "barcode")
    String barcode;
    @ColumnInfo(name = "description")
    String description;
    @ColumnInfo(name = "quantity")
    int quantity;
    @ColumnInfo(name = "image")
    String imageUri;
}
