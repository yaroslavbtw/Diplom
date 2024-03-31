package com.example.diplomaapp;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.health.connect.datatypes.Device;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diplomaapp.dataClasses.DBHelper;
import com.example.diplomaapp.dataClasses.Devices;
import com.example.diplomaapp.dataClasses.System;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class AddDevice extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 5164654;
    private DBHelper dbHelper;
    private String img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_device);

        Button buttonUpload = findViewById(R.id.buttonChangeImg);
        Button buttonSaveDevice = findViewById(R.id.buttonSaveDevice);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        buttonSaveDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText deviceFriendlyName = findViewById(R.id.textInputFriendlyName);
                TextInputEditText deviceIeeeid = findViewById(R.id.textInputDeviceIeeeid);
                TextInputEditText deviceType = findViewById(R.id.textInputDeviceType);

                String friendlyName = deviceFriendlyName.getText().toString();
                String ieeeid = deviceIeeeid.getText().toString();
                String type = deviceType.getText().toString();

                if(!TextUtils.isEmpty(friendlyName) && !TextUtils.isEmpty(ieeeid) && !TextUtils.isEmpty(type))
                {
                    dbHelper = new DBHelper(getApplicationContext());

                    Intent intent = getIntent();

                    String systemName = intent.getStringExtra("systemName");
                    String mqttUrl = intent.getStringExtra("mqttUrl");

                    System system = new System(systemName, mqttUrl);

                    Log.i("Add Device", ieeeid + type);
                    Devices device = new Devices(ieeeid, type, img);

                    dbHelper.addDevice(device, system);
                    dbHelper.close();

                    Intent deviceActivity = new Intent(".DevicesActivity");
                    startActivity(deviceActivity);
                }
                else Toast.makeText(getApplicationContext(), "All fields should be filled", Toast.LENGTH_LONG).show();;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImageView imgView = findViewById(R.id.imageViewDeviceAdd);
                imgView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}