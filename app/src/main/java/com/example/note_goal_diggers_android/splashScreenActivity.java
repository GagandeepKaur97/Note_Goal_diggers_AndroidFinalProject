package com.example.note_goal_diggers_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import pl.droidsonroids.gif.GifImageView;

public class splashScreenActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        GifImageView img = (GifImageView) findViewById(R.id.splashimage);

        img.setImageResource(R.drawable.notepadgif);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(splashScreenActivity.this,
                       MainActivity.class);

                startActivity(i);


                finish();

            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}
