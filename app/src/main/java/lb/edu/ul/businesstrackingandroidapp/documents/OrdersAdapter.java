package lb.edu.ul.businesstrackingandroidapp.documents;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lb.edu.ul.businesstrackingandroidapp.MainActivity;
import lb.edu.ul.businesstrackingandroidapp.R;
import lb.edu.ul.businesstrackingandroidapp.database.Order;

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DATE = 0;
    private static final int TYPE_ORDER = 1;


    private List<ListItem> items = new ArrayList<>();
    private Context context;

    public void submitList(Context context,List<ListItem> list){
        Log.d("ADAPTER", "submitList size = " + list.size());
        items.clear();
        items.addAll(list);
        this.context= context;
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof ListItem.DateHeader) {
            return TYPE_DATE;
        } else {
            return TYPE_ORDER;
        }
    }
    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;

        DateViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
        }

        void bind(ListItem.DateHeader header) {
            dateText.setText(header.date);
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView title, totalPrice;

        OrderViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.orderTitle);
            totalPrice = itemView.findViewById(R.id.totalPrice);
        }

        void bind(Order order) {
            title.setText(order.orderType + " №" + order.id);
            totalPrice.setText(String.valueOf(order.totalPrice));

            // Click to open details
            itemView.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("ORDER_ID", order.id);
                context.startActivity(intent);
            });
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_DATE) {
            View view = inflater.inflate(R.layout.item_date_header, parent, false);
            return new DateViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_order_document, parent, false);
            return new OrderViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("ADAPTER", "onBind position=" + position);
        ListItem item = items.get(position);

        if (holder instanceof DateViewHolder) {
            ((DateViewHolder) holder).bind((ListItem.DateHeader) item);
        } else {
            ((OrderViewHolder) holder).bind(((ListItem.OrderListItem) item).order);
        }
        Log.d("RV_BIND", "binding position " + position + " / size=" + items.size());

    }
    @Override
    public int getItemCount() {
        Log.d("getcount","size "+ items.size());
        return items.size();

    }


}
