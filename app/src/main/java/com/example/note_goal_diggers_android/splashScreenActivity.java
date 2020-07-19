package com.example.note_goal_diggers_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class splashScreenActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }
}