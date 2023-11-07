package com.cst3104.androidlab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static String TAG="MainActivity";
    EditText emailEditText;
    private SharedPreferences prefs;

    public final static String PREFERENCES_FILE = "MyData";

    @Override
    protected void onStart() {
        super.onStart();

        Log.w(TAG,"In onStart, visible but not responding");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.w(TAG,"In onResume, visible and is in foreground");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.w(TAG,"In onPause, partially visible and is going into the background");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.w(TAG,"In onStop, no longer visible but still alive in memory");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.w(TAG,"In onDestroy, the activity is destroyed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w("MainActivity","In onCreate() - Loading Widgets");

        Log.d(TAG,"Message");

        //transition from 1st page to 2nd page

        Button loginButton = findViewById(R.id.LoginButton);
        emailEditText = findViewById(R.id.emailEditText);

        //initialize SP
        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        //Load saved email
        String emailAddress = prefs.getString("LoginName","");
        emailEditText.setText(emailAddress);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailAddress = emailEditText.getText().toString();
                saveEmailAddress(emailAddress);
                Intent nextPage = new Intent(MainActivity.this, SecondActivity.class);

                //save email address when the button is clicked
                nextPage.putExtra("EmailAddress", emailAddress);

                startActivity(nextPage);
            }
        });
    }

    private  void saveEmailAddress(String emailAddress) {
        //get sharedPreferences.Editor
        SharedPreferences.Editor editor = prefs.edit();

        //save the email address to SharePreferences
        editor.putString("LoginName", emailAddress);

        //apply changes
        editor.apply();

    }
}