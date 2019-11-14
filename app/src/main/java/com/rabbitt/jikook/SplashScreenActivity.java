package com.rabbitt.jikook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    Handler splash = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splash.postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 1000);
    }
}