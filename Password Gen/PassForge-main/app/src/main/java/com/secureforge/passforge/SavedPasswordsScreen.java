package com.secureforge.passforge;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Map;

public class SavedPasswordsScreen extends AppCompatActivity {

    private TableLayout tableLayout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_passwords_screen);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tableLayout = findViewById(R.id.tableLayout);

        loadSavedPasswords();
    }

    private void loadSavedPasswords() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<Map<String, Object>> passwords = (List<Map<String, Object>>) documentSnapshot.get("passwords");

                            if (passwords != null) {
                                for (Map<String, Object> passwordData : passwords) {
                                    String websiteName = (String) passwordData.get("websiteName");
                                    String websiteLink = (String) passwordData.get("websiteLink");
                                    String password = (String) passwordData.get("password");

                                    addRowToTable(websiteName, websiteLink, password);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SavedPasswordsScreen.this, "Failed to load passwords", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(SavedPasswordsScreen.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void addRowToTable(String websiteName, String websiteLink, String password) {
        TableRow tableRow = new TableRow(this);

        TextView websiteNameTextView = new TextView(this);
        websiteNameTextView.setText(websiteName);
        websiteNameTextView.setPadding(16, 16, 16, 16);

        TextView websiteLinkTextView = new TextView(this);
        websiteLinkTextView.setText(websiteLink);
        websiteLinkTextView.setPadding(16, 16, 16, 16);

        TextView passwordTextView = new TextView(this);
        passwordTextView.setText(password);
        passwordTextView.setPadding(16, 16, 16, 16);

        tableRow.addView(websiteNameTextView);
        tableRow.addView(websiteLinkTextView);
        tableRow.addView(passwordTextView);

        tableLayout.addView(tableRow);
    }
}