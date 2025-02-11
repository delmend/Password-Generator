package com.secureforge.passforge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ImageButton dropdownButton = findViewById(R.id.imageButton);

        TextView txtSavedPasswordsCount = findViewById(R.id.txtSavedPasswordsCount);

        getSavedPasswordsCount(txtSavedPasswordsCount);

        Button buttonGeneratePassword = findViewById(R.id.buttonGeneratePassword);
        Button buttonViewSavedPasswords = findViewById(R.id.buttonViewSavedPasswords);

        buttonGeneratePassword.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GeneratePasswordScreen.class)));
        buttonViewSavedPasswords.setOnClickListener(v -> {
            getSavedPasswordsCount(txtSavedPasswordsCount);

            startActivity(new Intent(MainActivity.this, SavedPasswordsScreen.class));
        });

        dropdownButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.logout) {

                    mAuth.signOut();

                    new Handler().postDelayed(() -> {
                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        if (currentUser == null) {
                            startActivity(new Intent(MainActivity.this, Welcome_Screen.class));
                            finish();
                        }
                        Toast.makeText(MainActivity.this, "Logout completed", Toast.LENGTH_SHORT).show();
                    }, 2000);
                }

                if (item.getItemId() == R.id.settings) {
                    Toast.makeText(MainActivity.this, "Work in progress", Toast.LENGTH_SHORT).show();
                }

                return true;
            });

            popupMenu.show();
        });
    }

    private void getSavedPasswordsCount(TextView txtSavedPasswordsCount) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<Map<String, Object>> passwords = (List<Map<String, Object>>) documentSnapshot.get("passwords");

                            if (passwords != null) {
                                int passwordsCount = passwords.size();

                                txtSavedPasswordsCount.setText(String.valueOf(passwordsCount));
                            } else {
                                txtSavedPasswordsCount.setText("0");
                            }
                        } else {
                            txtSavedPasswordsCount.setText("0");
                            Toast.makeText(MainActivity.this, "User document not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        txtSavedPasswordsCount.setText("0");
                        Toast.makeText(MainActivity.this, "Failed to retrieve document", Toast.LENGTH_SHORT).show();
                    });
        } else {
            txtSavedPasswordsCount.setText("0");
            Toast.makeText(MainActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
