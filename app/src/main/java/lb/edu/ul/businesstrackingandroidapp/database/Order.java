package lb.edu.ul.businesstrackingandroidapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

 @Entity (tableName = "orders")
 public class Order {


    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "order_type")
    public OrderType orderType;

    @ColumnInfo(name = "total_price")
    public double totalPrice;

    @ColumnInfo(name = "order_date")
    public long orderDate; // System.currentTimeMillis()

}



