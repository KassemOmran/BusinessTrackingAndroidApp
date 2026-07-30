package lb.edu.ul.businesstrackingandroidapp;

import static lb.edu.ul.businesstrackingandroidapp.database.Converters.fromLocalDate;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lb.edu.ul.businesstrackingandroidapp.database.InventoryItem;






    class PlaceOrderAdapter extends RecyclerView.Adapter<PlaceOrderAdapter.ViewHolder> {

        private List<InventoryItem> items;
        private Context context;
        private boolean isOutgoing = true; // default

        public PlaceOrderAdapter(Context context, List<InventoryItem> items) {
            this.context = context;
            this.items = new ArrayList<>(items);
        }

        public void setOrderType(boolean outgoing) {
            this.isOutgoing = outgoing;
            notifyDataSetChanged();
        }

        public List<InventoryItem> getItems() {
            return items;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            ImageView itemImageView;
            TextView itemNameView, itemQuantityAvailable, itemExpiryDateView, itemPriceView;
            EditText itemQuantity;

            public ViewHolder(@NonNull View view) {
                super(view);

                itemImageView = view.findViewById(R.id.itemImageView);
                itemNameView = view.findViewById(R.id.itemNameView);
                itemQuantityAvailable = view.findViewById(R.id.itemQuantityAvailable);
                itemExpiryDateView = view.findViewById(R.id.itemExpiryDateView);
                itemPriceView = view.findViewById(R.id.itemPriceView);
                itemQuantity = view.findViewById(R.id.itemQuantity);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.place_order_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            InventoryItem item = items.get(position);

            holder.itemImageView.setImageResource(R.drawable.baseline_inventory_48);
            holder.itemNameView.setText(item.name);
            holder.itemQuantityAvailable.setText("Available: " + item.quantity);
            holder.itemExpiryDateView.setText("exp: " + fromLocalDate(item.expiryDate));
            holder.itemPriceView.setText("price: " + item.price);

            holder.itemQuantity.setText("0");
            holder.itemQuantity.setError(null);

            holder.itemQuantity.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int entered = 0;
                    try {
                        entered = Integer.parseInt(s.toString());
                    } catch (Exception ignored) {}

                    item.orderQuantity = entered;


                    if (isOutgoing && entered > item.quantity) {
                        holder.itemQuantity.setError("Quantity exceeds available stock");
                    } else {
                        holder.itemQuantity.setError(null);
                    }
                }

                @Override public void afterTextChanged(Editable s) {}
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

