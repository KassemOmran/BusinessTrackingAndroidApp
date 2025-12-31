package lb.edu.ul.businesstrackingandroidapp;

import static lb.edu.ul.businesstrackingandroidapp.database.Converters.fromLocalDate;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lb.edu.ul.businesstrackingandroidapp.database.InventoryItem;
import lb.edu.ul.businesstrackingandroidapp.databinding.ActivityAddEditInventoryBinding;



public class AddEditInventoryActivity extends AppCompatActivity {
    private ActivityAddEditInventoryBinding binding;
    private String oldBarcode;
    private InventoryItem oldItem;
    private LocalDate selectedExpiryDate;
    private Uri selectedImageUri=null;
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;

    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddEditInventoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        EdgeToEdge.enable(this);
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ActivityResultLauncher<String> pickImage =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null) {
                                selectedImageUri = uri;
                                binding.addItemImageText.setText("Image Selected!");
                            }
                        }
                );
        binding.addItemImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage.launch("image/*");
            }
        });

        Intent i = getIntent();
        oldBarcode = i.getStringExtra("barcode");
        id=-1;


        if (!(oldBarcode.equals("null"))) {

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    oldItem = InventoryActivity.db.inventoryItemDao().getItemByBarcode(oldBarcode);
                    binding.itemNameEd.setText(oldItem.name);
                    binding.itemBarcodeEd.setText(oldItem.barcode);
                    binding.itemDescriptionEd.setText(oldItem.description);
                    binding.itemQuantityEd.setText(""+oldItem.quantity);
                    binding.expiryDateEd.setText(fromLocalDate(oldItem.expiryDate));
                    binding.addItemImageText.setText(oldItem.imageUri);
                    selectedImageUri=Uri.parse(oldItem.imageUri);
                    finish();
                }
            });

        }

         barcodeLauncher= registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.itemBarcodeEd.setText(result.getContents());
                        dialog.dismiss();
                    }
                }).show();

            }



        });
        binding.qrCodeScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });
        EditText expiryDateEd = binding.expiryDateEd;
        expiryDateEd.setFocusable(false);
        expiryDateEd.setKeyListener(null);




        expiryDateEd.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(
                    this,
                    (view, y, m, d) -> {

                        // LocalDate uses 1-based month, DatePicker gives 0-based
                        selectedExpiryDate = LocalDate.of(y, m + 1, d);

                        // Display in EditText (ISO format — matches Room converter)
                        binding.expiryDateEd.setText(selectedExpiryDate.toString());
                    },
                    year, month, day
            );

            picker.show();
        });

    }

    public void saveButtonHandler(View v) {

        String name = binding.itemNameEd.getText().toString();
        String barcode = binding.itemBarcodeEd.getText().toString();
        LocalDate expiryDate = selectedExpiryDate;
        String description = binding.itemBarcodeEd.getText().toString();
        String quantityText=binding.itemQuantityEd.getText().toString().trim();
        String imageUri = selectedImageUri != null ? selectedImageUri.toString() : null;

        if(quantityText.isEmpty()){
            Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }
        int quantity = Integer.parseInt(quantityText);
        if(quantity<0){
            Toast.makeText(this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
            return;
        }
        if(oldItem==null){
            InventoryItem item = new InventoryItem();
            item.name=name;
            item.barcode=barcode;
            item.expiryDate=expiryDate;
            item.description=description;
            item.quantity=quantity;

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    InventoryActivity.db.inventoryItemDao().insertAll(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddEditInventoryActivity.this, "Save success",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

                }
            });
        }
        else {
            id = oldItem.id;
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    InventoryActivity.db.inventoryItemDao().updateInventoryItem(id,name,description, barcode,quantity,  expiryDate,imageUri);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddEditInventoryActivity.this, "Save success",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

                }
            });
        }


    }
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barcodeLauncher.launch(options);
    }

}