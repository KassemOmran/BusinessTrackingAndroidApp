package lb.edu.ul.businesstrackingandroidapp;

import static lb.edu.ul.businesstrackingandroidapp.database.Converters.fromLocalDate;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    private InventoryItem oldItem;
    private LocalDate selectedExpiryDate;
    private Uri selectedImageUri=null;
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;
    private Uri photoUri= null;
    ActivityResultLauncher<Uri> takePhoto;

    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddEditInventoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar.toolbar;
        setSupportActionBar(binding.toolbar.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());


        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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

        takePhoto =
                registerForActivityResult(
                        new ActivityResultContracts.TakePicture(),
                        success -> {
                            if (success) {
                                selectedImageUri = photoUri;
                            }
                        }
                );
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
        binding.cameraImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        Intent i = getIntent();
        id = i.getIntExtra("itemId",-1);


        if (id!=-1) {
            getSupportActionBar().setTitle("Edit Item");
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    oldItem = MainActivity.db.inventoryItemDao().getItemById(id);


                    binding.itemPriceEd.setText(""+oldItem.price);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(oldItem!=null){
                                binding.itemNameEd.setText(oldItem.name);
                                binding.itemBarcodeEd.setText(oldItem.barcode);
                                binding.itemDescriptionEd.setText(oldItem.description);
                                binding.itemQuantityEd.setText(""+oldItem.quantity);
                                binding.expiryDateEd.setText(fromLocalDate(oldItem.expiryDate));
                                selectedExpiryDate=oldItem.expiryDate;
                            }

                            if (oldItem != null && oldItem.imageUri != null && !oldItem.imageUri.isEmpty()) {
                                selectedImageUri = Uri.parse(oldItem.imageUri);
                                binding.addItemImageText.setText("Image Selected!");
                            } else {
                                selectedImageUri = null;
                                binding.addItemImageText.setText("No image selected");
                            }

                        }
                    });
                }
            });

        }
        else {
            getSupportActionBar().setTitle("Add New Item");
        }

         barcodeLauncher= registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                binding.itemBarcodeEd.setText(result.getContents());
            }



        });
        binding.qrCodeScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });


    }

    public void saveButtonHandler(View v) {

        String name = binding.itemNameEd.getText().toString();
        String barcode = binding.itemBarcodeEd.getText().toString();
        LocalDate expiryDate = selectedExpiryDate;
        String description = binding.itemDescriptionEd.getText().toString();
        String quantityText=binding.itemQuantityEd.getText().toString().trim();
        String imageUri = selectedImageUri != null ? selectedImageUri.toString() : null;
        String priceText = binding.itemPriceEd.getText().toString();

        if(quantityText.isEmpty()){
            Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }
        int quantity = Integer.parseInt(quantityText);
        if(quantity<0){
            Toast.makeText(this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
            return;
        }
        double price = Double.parseDouble(priceText);
        if(price<0){
            Toast.makeText(this, "Please enter a positive number for price", Toast.LENGTH_SHORT).show();
            return;
        }
        if(id==-1){
            InventoryItem item = new InventoryItem();
            item.name=name;
            item.barcode=barcode;
            item.expiryDate=expiryDate;
            item.description=description;
            item.quantity=quantity;
            item.price=price;
            item.imageUri=imageUri;

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    MainActivity.db.inventoryItemDao().insertAll(item);
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

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    MainActivity.db.inventoryItemDao().updateInventoryItem(id,name,description, barcode,quantity,price  ,expiryDate,imageUri);
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
    void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "item_" + System.currentTimeMillis());

        photoUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
        );

        selectedImageUri = photoUri;

        takePhoto.launch(photoUri);
    }

}