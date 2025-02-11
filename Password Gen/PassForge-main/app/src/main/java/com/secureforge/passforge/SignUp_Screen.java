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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SignUp_Screen extends AppCompatActivity {

    private EditText editEmail, editPassword, editRePassword;
    private ProgressBar progressBar;
    private String register_time, userID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

//        TextView txtSignUp = findViewById(R.id.txtSignUp);
//        TextView txtFillDetails = findViewById(R.id.txtFillDetails);
//        TextView txtAccount = findViewById(R.id.txtAccount);
        TextView txtSignInLink = findViewById(R.id.txtSignInLink);


        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editRePassword = findViewById(R.id.editRePassword);

        Button btnSignUp = findViewById(R.id.btnSignUp);

        progressBar = findViewById(R.id.pbarSignup);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        txtSignInLink.setOnClickListener(v -> {
            Intent SignInIntent = new Intent(SignUp_Screen.this, SignIn_Screen.class);
            SignInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(SignInIntent);
            finish();

        });

        btnSignUp.setOnClickListener(v -> {
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            String repass = editRePassword.getText().toString();
            SimpleDateFormat formater = new SimpleDateFormat("EEE, d, MMM, yyyy 'at' h:mm:s a", Locale.UK);
            register_time = formater.format(new Date(Long.parseLong(String.valueOf(System.currentTimeMillis()))));

            if (TextUtils.isEmpty(email)) {
                editEmail.setError("Email is required");
            }

            if (TextUtils.isEmpty(password)) {
                editPassword.setError("Password is required");
            }

            if (password.length() <= 10) {
                editPassword.setError("More than 10 digits is required");
            }

            if (TextUtils.isEmpty(repass)) {
                editRePassword.setError("Email is required");
            }

            if (repass.length() <= 10) {
                editRePassword.setError("More than 10 digits is required");
            }

            if (repass.equals(password)) {
                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                        DocumentReference documentReference = firestore.collection("users").document(userID);
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("email", email);
                        userMap.put("password", password);
                        userMap.put("created_at", register_time);
                        userMap.put("id", userID);

                        documentReference.set(userMap).addOnSuccessListener(unused -> {
                            Intent setupIntent = new Intent(SignUp_Screen.this, MainActivity.class);
                            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(setupIntent);
                            Toast.makeText(SignUp_Screen.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(SignUp_Screen.this, "Error : " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                });
            } else {
                editRePassword.setError("Password Must Match");
            }

        });
    }
}