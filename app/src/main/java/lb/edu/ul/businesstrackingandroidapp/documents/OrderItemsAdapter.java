package lb.edu.ul.businesstrackingandroidapp.documents;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import lb.edu.ul.businesstrackingandroidapp.MainActivity;
import lb.edu.ul.businesstrackingandroidapp.R;
import lb.edu.ul.businesstrackingandroidapp.database.InventoryItem;
import lb.edu.ul.businesstrackingandroidapp.database.OrderItem;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ItemViewHolder> {

    private List<OrderItem> items = new ArrayList<>();

    public void submitList(List<OrderItem> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        OrderItem item = items.get(position);

        Executors.newSingleThreadExecutor().execute(() -> {
            InventoryItem i = MainActivity.db.inventoryItemDao().getItemById(item.itemId);
            holder.itemView.post(() -> holder.name.setText(i.name));
        });

        holder.qty.setText("x" + item.quantity);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, qty;

        ItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.orderItemName);
            qty = itemView.findViewById(R.id.orderItemQuantity);
        }


    }
}
