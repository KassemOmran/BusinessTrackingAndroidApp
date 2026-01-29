package lb.edu.ul.businesstrackingandroidapp.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lb.edu.ul.businesstrackingandroidapp.R;

public class NotificationAdapter
        extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<NotificationItem> notifications = new ArrayList<>();

    public void setNotifications(List<NotificationItem> list) {
        notifications.clear();
        notifications.addAll(list);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title, message;

        ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            title = view.findViewById(R.id.title);
            message = view.findViewById(R.id.message);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        NotificationItem item = notifications.get(position);

        holder.title.setText(item.title);
        holder.message.setText(item.message);

        if (item.type == NotificationItem.TYPE_LOW_STOCK) {
            holder.icon.setImageResource(R.drawable.ic_low_stock);
        } else {
            holder.icon.setImageResource(R.drawable.ic_expiry);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
