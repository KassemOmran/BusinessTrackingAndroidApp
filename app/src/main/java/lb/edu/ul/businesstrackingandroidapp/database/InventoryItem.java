package lb.edu.ul.businesstrackingandroidapp.database;

import android.media.Image;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class InventoryItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "barcode")
    public String barcode;
    @ColumnInfo(name = "description")
    public String description;
    @ColumnInfo(name = "expirydate")
    public LocalDate expiryDate;
    @ColumnInfo(name = "price")
    public double price;
    @ColumnInfo(name = "quantity")
    public int quantity;
    @ColumnInfo(name = "image")
    public String imageUri;
    @Ignore
    public int orderQuantity;

}
