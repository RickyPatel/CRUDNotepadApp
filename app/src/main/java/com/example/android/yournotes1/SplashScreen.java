package com.example.android.yournotes1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT =5000;
    TextView from, devsoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        devsoc = findViewById(R.id.devsoc);
        from = findViewById(R.id.from);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashintent =new Intent(SplashScreen.this, StartActivity.class);
                startActivity(splashintent);
            }
        },SPLASH_TIME_OUT);
        YoYo.with(Techniques.FadeOutDown).duration(5100).repeat(0).playOn(from);
        YoYo.with(Techniques.FadeInDown).duration(5100).repeat(0).playOn(devsoc);
    }
}
