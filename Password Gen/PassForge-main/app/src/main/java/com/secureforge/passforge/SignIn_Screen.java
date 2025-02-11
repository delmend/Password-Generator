package com.secureforge.passforge;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignIn_Screen extends AppCompatActivity {
    private EditText editEmail, editPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        TextView txtForgot = findViewById(R.id.txtForgot);
        TextView txtSignUpLink = findViewById(R.id.txtSignUpLink);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        Button btnSignIn = findViewById(R.id.buttonSignIn);
        progressBar = findViewById(R.id.progressBar);

        txtSignUpLink.setOnClickListener(v -> {
            Intent SignupIntent = new Intent(SignIn_Screen.this, SignUp_Screen.class);
            SignupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(SignupIntent);
            finish();
        });

        btnSignIn.setOnClickListener(v -> {
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                editEmail.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editPassword.setError("Password is required");
                return;
            }

            if (password.length() <= 10) {
                editPassword.setError("10 characters or more are required");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    sendToMainActivity();
                } else {
                    String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(SignIn_Screen.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        txtForgot.setOnClickListener(v -> {
            Intent ForgotIntent = new Intent(SignIn_Screen.this, Forgot_Screen.class);
            ForgotIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(ForgotIntent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            sendToMainActivity();
        }
    }

    private void sendToMainActivity() {
        Intent StartIntent = new Intent(SignIn_Screen.this, MainActivity.class);
        StartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(StartIntent);
        finish();
    }
}
