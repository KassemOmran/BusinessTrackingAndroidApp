package lb.edu.ul.businesstrackingandroidapp;

import static lb.edu.ul.businesstrackingandroidapp.database.Converters.fromLocalDate;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import lb.edu.ul.businesstrackingandroidapp.database.InventoryItem;

public class InventoyItemsAdapter extends RecyclerView.Adapter<InventoyItemsAdapter.ViewHolder> {
    private List<InventoryItem> items;
    private List<InventoryItem> itemsFull;
    Context context;
    FragmentManager fragmentManager;
    public InventoyItemsAdapter(Context context, List<InventoryItem> items, FragmentManager fragmentManager){
        this.context = context;
        this.fragmentManager = fragmentManager;

        this.items = new ArrayList<>(items);       // create a new list for displayed items
        this.itemsFull = new ArrayList<>(items);   // create a **separate** copy for backup

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;
        private final TextView barcodeView;
        private final TextView expiryDateView;
        private final ImageView itemImageView;
        private final TextView quantityView;
        public ViewHolder(View view,Context context, FragmentManager fragmentManager){
            super(view);
            nameView= view.findViewById(R.id.itemNameView);
            barcodeView=view.findViewById(R.id.itemBarcodeView);
            itemImageView=view.findViewById(R.id.itemImageView);
            expiryDateView=view.findViewById(R.id.itemExpiryDateView);
            quantityView=view.findViewById(R.id.itemQuantityView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context,AddEditInventoryActivity.class);
                    i.putExtra("barcode",barcodeView.getText().toString());
                    context.startActivity(i);
                }
            });
        }

        public TextView getNameView() {
            return nameView;
        }

        public TextView getBarcodeView() {
            return barcodeView;
        }

        public ImageView getItemImageView() {
            return itemImageView;
        }

        public TextView getExpiryDateView() {
            return expiryDateView;
        }

        public TextView getQuantityView() {
            return quantityView;
        }
    }

    @NonNull
    @Override
    public InventoyItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_inventory,parent,false);
        return new ViewHolder(view,context,fragmentManager);
    }




    @Override
    public void onBindViewHolder(@NonNull InventoyItemsAdapter.ViewHolder holder, int position) {
        String expiryDAte = fromLocalDate(items.get(position).expiryDate);
        String uriString = items.get(position).imageUri;

        if (uriString != null && !uriString.isEmpty()) {
            Uri imageUri = Uri.parse(uriString);
            holder.getItemImageView().setImageURI(imageUri);
        } else {
            holder.getItemImageView().setImageResource(R.drawable.baseline_image_24);
        }

        holder.getNameView().setText(items.get(position).name);
        holder.getBarcodeView().setText(items.get(position).barcode);
        holder.getExpiryDateView().setText(expiryDAte);
        holder.getQuantityView().setText(items.get(position).quantity+"");

    }

    @Override
    public int getItemCount() {
        return items.size();

    }

    public Filter getFilter(){
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<InventoryItem> filteredList = new ArrayList<>(); // always non-null
                String query = constraint.toString().toLowerCase().trim();

                if (query.isEmpty()) {
                    filteredList.addAll(itemsFull);  // safe, itemListFull should never be null
                } else {
                    for (InventoryItem item : itemsFull) {
                        if (item.name.toLowerCase().contains(query)||item.description.toLowerCase().contains(query)||item.barcode.toLowerCase().contains(query)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList; // always non-null
                return results;
            }


            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                items.clear();
                if (results.values != null) {
                    items.addAll((List<InventoryItem>) results.values);
                }
                notifyDataSetChanged();
            }

        };
    }




}
