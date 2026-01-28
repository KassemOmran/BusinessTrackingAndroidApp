package lb.edu.ul.businesstrackingandroidapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Calendar;

import lb.edu.ul.businesstrackingandroidapp.MainActivity;
import lb.edu.ul.businesstrackingandroidapp.R;
import lb.edu.ul.businesstrackingandroidapp.database.OrderDao;
import lb.edu.ul.businesstrackingandroidapp.database.OrderType;

public class BlankFragment extends Fragment {

    private static final String ARG_DATE_RANGE = "dateRange";

    private String dateRange;
    private OrderDao orderDao;

    public static BlankFragment newInstance(String dateRange) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE_RANGE, dateRange);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dateRange = getArguments().getString(ARG_DATE_RANGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blank, container, false);


        loadReport(view);

        return view;
    }

    private void loadReport(View view) {
        TextView tvReport = view.findViewById(R.id.tvReport);

        long[] range = resolveDateRange(dateRange);
        long start = range[0];
        long end = range[1];

        new Thread(() -> {
            Double total =
                    MainActivity.db.orderDao().getTotalSalesByDateRange(
                            OrderType.OUTGOING,
                            start,
                            end
                    );

            if (total == null) total = 0.0;

            Double finalTotal = total;

            requireActivity().runOnUiThread(() -> {
                tvReport.setText(
                        "Sales Report\n\n" +
                                "Date range: " + dateRange + "\n\n" +
                                "Total sales: $" + finalTotal
                );
            });
        }).start();
    }

    private long[] resolveDateRange(String dateRange) {
        Calendar cal = Calendar.getInstance();
        long end = cal.getTimeInMillis();

        switch (dateRange) {
            case "Current month":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                break;

            case "Last 30 days":
                cal.add(Calendar.DAY_OF_YEAR, -30);
                break;

            case "Last 90 days":
                cal.add(Calendar.DAY_OF_YEAR, -90);
                break;

            case "Current year":
                cal.set(Calendar.DAY_OF_YEAR, 1);
                break;

            case "All time":
                cal.setTimeInMillis(0);
                break;
        }

        long start = cal.getTimeInMillis();
        return new long[]{start, end};
    }
}
