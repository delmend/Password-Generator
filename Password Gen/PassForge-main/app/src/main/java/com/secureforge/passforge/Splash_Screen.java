package com.secureforge.passforge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash_Screen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_DURATION = 4000;
    TextView com_name, com_slogan;
    ImageView com_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        com_name = findViewById(R.id.txt_app_name);
        com_slogan = findViewById(R.id.txt_app_slogan);

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isFirstRun){
                    startActivity(new Intent(Splash_Screen.this, SignIn_Screen.class));
                    finish();
                } else {
                    startActivity(new Intent(Splash_Screen.this,Welcome_Screen.class));
                    getSharedPreferences("PREFERENCES",MODE_PRIVATE).edit().putBoolean("isFirstRun",false).commit();
                    finish();
                }
            }
        }, SPLASH_DISPLAY_DURATION);

        Animation slideInRight = AnimationUtils.loadAnimation(this,R.anim.slide_in);
        com_name.startAnimation(slideInRight);

        Animation slideInLeft = AnimationUtils.loadAnimation(this,R.anim.slide_in_left);
        com_slogan.startAnimation(slideInLeft);
    }
}