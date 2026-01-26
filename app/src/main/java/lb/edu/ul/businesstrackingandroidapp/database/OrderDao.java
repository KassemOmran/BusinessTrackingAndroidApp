package lb.edu.ul.businesstrackingandroidapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.lifecycle.LiveData;


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
            InventoryItemDao inventoryItemDao
    ) {
        long orderId = insertOrder(order);

        for (OrderItem item : items) {
            item.orderId = (int) orderId;
            insertOrderItem(item);

            InventoryItem inventoryItem =
                    inventoryItemDao.getItemById(item.itemId);

            if (order.orderType == OrderType.OUTGOING) {
                inventoryItem.quantity -= item.quantity;
            } else {
                inventoryItem.quantity += item.quantity;
            }

            inventoryItemDao.update(inventoryItem);
        }
    }
    @Query("SELECT * FROM orders")
    LiveData<List<Order>> getAllOrders();
    @Query("SELECT * FROM order_items WHERE orderId = :orderID")
    List<OrderItem> getAllOrderItemForOrder(int orderID);
    @Query("SELECT * FROM orders WHERE id= :orderId")
    Order getOrderById(int orderId);
}

