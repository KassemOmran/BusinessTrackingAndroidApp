package lb.edu.ul.businesstrackingandroidapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert
    long insertOrder(Order order);

    @Insert
    void insertOrderItem(OrderItem item);


    @Transaction
    default void insertOrderWithItems(
            Order order,
            List<OrderItem> items,
            InventoryDao inventoryDao
    ) {
        long orderId = insertOrder(order);

        for (OrderItem item : items) {
            item.orderId = (int) orderId;
            insertOrderItem(item);

            InventoryItem inventoryItem =
                    inventoryDao.getItemById(item.itemId);

            if (order.orderType == OrderType.OUTGOING) {
                inventoryItem.quantity -= item.quantity;
            } else {
                inventoryItem.quantity += item.quantity;
            }

            inventoryDao.update(inventoryItem);
        }
    }

}

