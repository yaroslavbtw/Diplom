package com.example.diplomaapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.example.diplomaapp.dataClasses.Storage;
import com.example.diplomaapp.dataClasses.System;
import com.example.diplomaapp.databinding.ActivityAddDeviceBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

public class AddDevice extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 5164654;
    private DBHelper dbHelper;
    private String img = "";
    private ActivityAddDeviceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_device);

        binding = ActivityAddDeviceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(Storage.device != null)
        {
            binding.textInputFriendlyName.setText(Storage.device.getFriendlyName());
            binding.textInputMqttPrefix.setText(Storage.device.getMqttPrefix());
            binding.textInputDeviceIeeeid.setText(Storage.device.getDeviceId());
            binding.textInputDeviceType.setText(Storage.device.getType());
            if(!Objects.equals(Storage.device.getDiodeChannel(), ""))
                binding.textInputDeviceSwitchChannel.setText(Storage.device.getDiodeChannel());
            if (Storage.device.getImgPath() != null){
                if (!Storage.device.getImgPath().isEmpty()) {
                    try {
                        File file = new File(Storage.device.getImgPath());
                        if (file.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            ImageView imgView = findViewById(R.id.imageViewDeviceAdd);
                            imgView.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

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
                boolean isFriendlyName =  Objects.requireNonNull(binding.textInputFriendlyName.getText()).toString().isEmpty();
                boolean isDeviceIeeeid = Objects.requireNonNull(binding.textInputDeviceIeeeid.getText()).toString().isEmpty();
                boolean isDeviceType = Objects.requireNonNull(binding.textInputDeviceType.getText()).toString().isEmpty();
                boolean isMqttPrefix = Objects.requireNonNull(binding.textInputMqttPrefix.getText()).toString().isEmpty();
                boolean isDiodeChannel = Objects.requireNonNull(binding.textInputDeviceSwitchChannel.getText()).toString().isEmpty();

                if(!isFriendlyName && !isDeviceIeeeid && !isDeviceType && !isMqttPrefix)
                {
                    dbHelper = new DBHelper(getApplicationContext());
                    String deviceIeeeid = binding.textInputDeviceIeeeid.getText().toString();

                    if(Storage.device == null || !Objects.equals(Storage.device.getDeviceId(), deviceIeeeid)){
                        if(dbHelper.isDeviceExists(deviceIeeeid))
                            Toast.makeText(getApplicationContext(), "This device id is already exists", Toast.LENGTH_LONG).show();
                        else saveDevice(deviceIeeeid, isDiodeChannel);
                    }else saveDevice(deviceIeeeid, isDiodeChannel);

                }
                else Toast.makeText(getApplicationContext(), "All fields should be filled", Toast.LENGTH_LONG).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveDevice(String deviceIeeeid, Boolean isDiodeChannel){
        Intent intent = getIntent();

        String systemName = intent.getStringExtra("systemName");
        String mqttUrl = intent.getStringExtra("mqttUrl");

        System system = new System(systemName, mqttUrl);

        String friendlyName = binding.textInputFriendlyName.getText().toString();

        String deviceType = binding.textInputDeviceType.getText().toString();
        String deviceMqttPrefix = binding.textInputMqttPrefix.getText().toString();

        Devices device;

        if(Storage.device != null)
            if(!Objects.equals(img, ""))
                device = new Devices(deviceIeeeid, deviceType, img);
            else device = new Devices(deviceIeeeid, deviceType, Storage.device.getImgPath());
        else device = new Devices(deviceIeeeid, deviceType, img);

        device.setFriendlyName(friendlyName);
        device.setMqttPrefix(deviceMqttPrefix);
        if(!isDiodeChannel)
        {
            String diodeChannel = binding.textInputDeviceSwitchChannel.getText().toString();
            device.setDiodeChannel(diodeChannel);
        }
        if(Storage.device != null){
            dbHelper.updateDevice(device, Storage.device);
            Toast.makeText(getApplicationContext(), "Successfully updated device!", Toast.LENGTH_LONG).show();
        }else{
            dbHelper.addDevice(device, system);
            Toast.makeText(getApplicationContext(), "Successfully added device!", Toast.LENGTH_LONG).show();
        }
        dbHelper.close();
        finish();
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // Сохраняем изображение в файловой системе устройства
                img = saveImageToInternalStorage(selectedImage, imageUri);
                Log.i("sql img", img);
                // Загружаем изображение в ImageView
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

    private String saveImageToInternalStorage(Bitmap bitmapImage, Uri imageUri){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        String uniqueFileName = "profile_" + UUID.randomUUID().toString();

        // Извлекаем расширение из URI
        String extension = getExtensionFromUri(imageUri);
        if (extension != null && !extension.isEmpty()) {
            uniqueFileName += "." + extension;
        } else {
            // Если не удалось извлечь расширение, используем JPEG по умолчанию
            uniqueFileName += ".jpg";
        }

        File mypath = new File(directory, uniqueFileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Сжимаем изображение в соответствии с расширением
            if (extension != null && extension.equalsIgnoreCase("png")) {
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } else {
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath() + "/" + uniqueFileName;
    }

    private String getExtensionFromUri(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension;

        if (contentResolver == null || uri == null) {
            return null;
        }

        String mimeType = contentResolver.getType(uri);
        if (mimeType == null) {
            String uriString = uri.toString();
            int extensionIndex = uriString.lastIndexOf('.');
            if (extensionIndex != -1 && extensionIndex < uriString.length() - 1) {
                extension = uriString.substring(extensionIndex + 1);
                return extension.toLowerCase();
            }
            return null;
        }

        // Получаем расширение из MIME типа
        extension = mimeTypeMap.getExtensionFromMimeType(mimeType);
        return extension;
    }

    @Override
    protected void onDestroy() {
        Storage.device = null;
        binding = null;
        super.onDestroy();
    }

}