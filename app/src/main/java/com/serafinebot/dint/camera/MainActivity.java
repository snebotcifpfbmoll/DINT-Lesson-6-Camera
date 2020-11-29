package com.serafinebot.dint.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 1990;
    private static final int CAMERA_CODE = 1888;
    private ImageView imageView = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = findViewById(R.id.cameraButton);
        Button saveButton = findViewById(R.id.saveButton);
        imageView = findViewById(R.id.imageView);

        cameraButton.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_CODE);
            }
        });

        saveButton.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                String msg = "Failed to save image";
                try {
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    String path = Environment.getExternalStorageDirectory().toString() + "/result.png";
                    File filename = new File(path);
                    filename.getParentFile().mkdirs();
                    FileOutputStream out = new FileOutputStream(filename);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    msg = "Saved image.";
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            } else {
                Log.d(LOG_TAG, "requesting permission for sd card");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_CODE);
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            case EXTERNAL_STORAGE_PERMISSION_CODE:
                String msg = "External storage permission denied.";
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    msg = "External storage permission granted.";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                break;
            default:
                Log.d(LOG_TAG, "Unknown request code.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }
}