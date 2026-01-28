package lb.edu.ul.businesstrackingandroidapp.documents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lb.edu.ul.businesstrackingandroidapp.database.DateUtils;
import lb.edu.ul.businesstrackingandroidapp.database.Order;

public class OrderListBuilder {

    private OrderListBuilder() {
        // Prevent instantiation
    }

    public static List<ListItem> buildListWithDates(List<Order> orders) {
        List<ListItem> result = new ArrayList<>();
        Map<Long, List<Order>> grouped = new LinkedHashMap<>();

        for (Order order : orders) {
            long dayKey = DateUtils.getDayTimestamp(order.orderDate);

            if (!grouped.containsKey(dayKey)) {
                grouped.put(dayKey, new ArrayList<>());
            }
            grouped.get(dayKey).add(order);
        }

        for (Long dayKey : grouped.keySet()) {
            String dateText = DateUtils.formatDate(dayKey);
            result.add(new ListItem.DateHeader(dateText));

            for (Order order : grouped.get(dayKey)) {
                result.add(new ListItem.OrderListItem(order));
            }
        }

        return result;
    }


}

