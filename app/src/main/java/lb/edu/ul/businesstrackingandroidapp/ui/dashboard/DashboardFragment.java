package lb.edu.ul.businesstrackingandroidapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lb.edu.ul.businesstrackingandroidapp.AddEditInventoryActivity;
import lb.edu.ul.businesstrackingandroidapp.CaptureAct;
import lb.edu.ul.businesstrackingandroidapp.InventoryActivity;
import lb.edu.ul.businesstrackingandroidapp.MainActivity;
import lb.edu.ul.businesstrackingandroidapp.database.InventoryItem;
import lb.edu.ul.businesstrackingandroidapp.databinding.FragmentDashboardBinding;
import lb.edu.ul.businesstrackingandroidapp.PlaceOrderActivity;
import lb.edu.ul.businesstrackingandroidapp.documents.DocumentsActivity;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private CardView scannerCard;
    private CardView inventoryCard;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scannerCard = binding.scannerCard;

        scannerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });
        binding.inventoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), InventoryActivity.class);
                startActivity(i);
            }
        });
        binding.placeOrderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PlaceOrderActivity.class);
                startActivity(i);
            }
        });
        binding.documentsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DocumentsActivity.class);
                startActivity(i);
            }
        });
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barcodeLauncher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String barcode = result.getContents();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                InventoryItem item = MainActivity.db.inventoryItemDao().getItemByBarcode(barcode);

                if (item != null) {
                    int id = item.id;

                    // Switch back to main thread for UI work
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Intent i = new Intent(getContext(), AddEditInventoryActivity.class);
                        i.putExtra("itemId", id);
                        startActivity(i);
                    });
                } else {
                    // Handle case where item is not found
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }




    });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}