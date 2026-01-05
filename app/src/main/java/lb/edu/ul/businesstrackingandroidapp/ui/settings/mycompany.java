package lb.edu.ul.businesstrackingandroidapp.ui.settings;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import lb.edu.ul.businesstrackingandroidapp.R;

public class mycompany extends AppCompatActivity {

    private Button picture;
    private Uri cameraImageUri;

    // ✅ MUST be here (class level)
    private ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Uri imageUri;

                            if (result.getData() != null &&
                                    result.getData().getData() != null) {
                                imageUri = result.getData().getData(); // Gallery
                            } else {
                                imageUri = cameraImageUri; // Camera
                            }

                            // TODO: use imageUri (ImageView, upload, etc.)
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mycompany);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mycomany), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        picture = findViewById(R.id.Picture);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");

                cameraImageUri = getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);

                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

                Intent chooser = Intent.createChooser(galleryIntent, "Select Image");
                chooser.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[]{cameraIntent}
                );

                imagePickerLauncher.launch(chooser);
            }
        });
    }
}