package com.secureforge.passforge;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Screen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_screen);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnSend = findViewById(R.id.btnSend);

        TextView editEmail = findViewById(R.id.editEmail);

        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(v -> {
            Intent backIntent = new Intent(Forgot_Screen.this, SignIn_Screen.class);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backIntent);
            finish();
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    editEmail.setError("Email is required");
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SuccessDialog();
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        FailedDialog();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            private void FailedDialog() {
                new AlertDialog.Builder(Forgot_Screen.this)
                        .setTitle("Confirmation")
                        .setCancelable(false)
                        .setMessage("Failed to send reset password email!\nPlease Try Again")
                        .setPositiveButton("OK", (dialog, i) -> dialog.dismiss()).create().show();
            }

            private void SuccessDialog() {
                new AlertDialog.Builder(Forgot_Screen.this)
                        .setTitle("Confirmation")
                        .setCancelable(false)
                        .setMessage("Check your email and follow the instructions to reset your password")
                        .setPositiveButton("OK", (dialog, i) -> {
                            dialog.dismiss();
                            Intent SignInIntent = new Intent(Forgot_Screen.this, SignIn_Screen.class);
                            SignInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(SignInIntent);
                            finish();
                        }).create().show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent SignInIntent = new Intent(Forgot_Screen.this, SignIn_Screen.class);
        SignInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(SignInIntent);
        finish();
    }
}