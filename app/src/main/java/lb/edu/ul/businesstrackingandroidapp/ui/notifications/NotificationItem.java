package lb.edu.ul.businesstrackingandroidapp.ui.notifications;

public class NotificationItem {

    public static final int TYPE_LOW_STOCK = 0;
    public static final int TYPE_EXPIRY = 1;

    public int type;
    public String title;
    public String message;

    public NotificationItem(int type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
    }
}
