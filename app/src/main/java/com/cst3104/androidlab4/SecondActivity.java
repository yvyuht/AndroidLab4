package com.cst3104.androidlab4;

import static android.Manifest.permission.CALL_PHONE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SecondActivity extends AppCompatActivity {
    private ImageView profileImage;
    private EditText phoneNumberEditText;
    private SharedPreferences prefs;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityResultLauncher<Intent> cameraResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //take image
        profileImage = findViewById(R.id.profileImage);
        Button changePicture = findViewById(R.id.changePicButton);

        changePicture.setOnClickListener(view -> dispatchTakePictureIntent());


        //Check file exists
        File file = new File(getFilesDir(), "Picture.png");
        if (file.exists()) {
            Bitmap loadedBitmap = loadBitmapFromFile(file);
            profileImage.setImageBitmap(loadedBitmap);
        }


        // welcome email
        Intent fromPrevious = getIntent();
        String emailAddress = fromPrevious.getStringExtra("EmailAddress");

        TextView userInput = findViewById(R.id.textView2);
        if (!emailAddress.isEmpty()) {
            userInput.setText("Welcome back " + emailAddress);
        }


        //make a phone call
        Button makeCall = findViewById(R.id.callNumberbutton);

        makeCall.setOnClickListener((click) -> {
            EditText userNumber = findViewById(R.id.editTextPhone);
            String phoneNumber = userNumber.getText().toString();

            String url = "tel:" + phoneNumber;
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse(url));


            if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                startActivity(i);
            } else {
                requestPermissions(new String[]{CALL_PHONE}, 1);
            }
        });

        //save phone number
        phoneNumberEditText = findViewById(R.id.editTextPhone);

        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        //load saved phone number
        String phoneNumber = prefs.getString("PhoneNumber", "");
        phoneNumberEditText.setText(phoneNumber);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void saveBitmapToFile(Bitmap bitmap) {
        FileOutputStream fOut;
        try {
            fOut = openFileOutput("Picture.png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadBitmapFromFile(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //save captured image to internal storage
            saveBitmapToFile(imageBitmap);

            //set captured image to ImageView
            profileImage.setImageBitmap(imageBitmap);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //save phone number
        String phoneNumber = phoneNumberEditText.getText().toString();
        savePhoneNumber(phoneNumber);

    }

    private void savePhoneNumber(String phoneNumber) {
        //get SP
        SharedPreferences.Editor editor = prefs.edit();

        //save to SP
        editor.putString("PhoneNumber", phoneNumber);

        //Apply changes
        editor.apply();
    }
}