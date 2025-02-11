package com.secureforge.passforge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome_Screen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button btn_signIn = findViewById(R.id.buttonSignIn);

        btn_signIn.setOnClickListener(v -> {
            Intent signIn_Intent = new Intent(Welcome_Screen.this, SignIn_Screen.class);
            startActivity(signIn_Intent);
        });

        Button btn_signUp = findViewById(R.id.buttonSignUp);

        btn_signUp.setOnClickListener(v -> {
            Intent signIn_Intent = new Intent(Welcome_Screen.this, SignUp_Screen.class);
            startActivity(signIn_Intent);
        });
    }
}