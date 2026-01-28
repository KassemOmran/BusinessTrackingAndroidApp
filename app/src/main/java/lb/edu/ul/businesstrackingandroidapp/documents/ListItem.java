package lb.edu.ul.businesstrackingandroidapp.documents;

import java.util.ArrayList;
import java.util.List;

import lb.edu.ul.businesstrackingandroidapp.MainActivity;
import lb.edu.ul.businesstrackingandroidapp.database.Order;
import lb.edu.ul.businesstrackingandroidapp.database.OrderItem;

public abstract class ListItem {
    public static class DateHeader extends ListItem{
        public String date;
        public DateHeader(String date){
            this.date = date;
        }
    }
    public static class OrderListItem extends ListItem{
        public Order order;
        public OrderListItem(Order order){
            this.order = order;
        }
    }
}
