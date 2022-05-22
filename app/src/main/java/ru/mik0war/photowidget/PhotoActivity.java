package ru.mik0war.photowidget;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PhotoActivity extends AppCompatActivity {

    private Button makePhotoButton;
    private Button loadPhotoButton;

    ActivityResultLauncher<Intent> photoPickLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);

        setActionBar();

        makePhotoButton = this.findViewById(R.id.btn_make_photo);
        makePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result){
                            Log.e(this.getClass().getSimpleName(), "permission granted");
                        }
                        else{
                            Toast.makeText(getApplicationContext(),
                                    "Some permission denied", Toast.LENGTH_LONG).show();
                        }
                    }
                });


        loadPhotoButton = this.findViewById(R.id.btn_load_photo);

        loadPhotoButton.setOnClickListener(view -> {

            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");

            photoPickLauncher.launch(photoPickerIntent);
        });

         photoPickLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                 result -> {
                     if (result.getResultCode() == Activity.RESULT_OK) {
                         try {
                             final Uri imageUri = result.getData().getData();
                             final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                             final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                             ImageFileExplorer imageFileExplorer = new ImageFileExplorer(getApplicationContext())
                                     .setExternal(true)
                                     .setFileFormat(Bitmap.CompressFormat.PNG)
                                     .setFileName();

                             imageFileExplorer.save(selectedImage);

                         } catch (FileNotFoundException e) {
                             e.printStackTrace();
                             Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                         }


                     }
                 });

    }


    public void openSomeActivityForResult() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoPickLauncher.launch(cameraIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.photo_widget_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Button button = findViewById(R.id.action_settings);
    }
}
