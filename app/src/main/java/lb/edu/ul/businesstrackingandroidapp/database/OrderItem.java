package lb.edu.ul.businesstrackingandroidapp.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "order_items",
        foreignKeys = {
                @ForeignKey(
                        entity = Order.class,
                        parentColumns = "id",
                        childColumns = "orderId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = InventoryItem.class,
                        parentColumns = "id",
                        childColumns = "itemId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("orderId"),
                @Index("itemId")
        }
)
public class OrderItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int orderId;   // FK → Order
    public int itemId;    // FK → InventoryItem

    public int quantity;

    public double unitPrice;
}

