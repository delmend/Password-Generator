package com.secureforge.passforge;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GeneratePasswordScreen extends AppCompatActivity {

    private SeekBar seekBarLength;
    private EditText txtLengthValue;
    private CheckBox checkboxUppercase;
    private CheckBox checkboxLowercase;
    private CheckBox checkboxNumbers;
    private CheckBox checkboxSpecialChars;
    private TextView txtGeneratedPassword;
    private ProgressBar passwordStrengthMeter;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_password_screen);

        seekBarLength = findViewById(R.id.seekBarLength);
        txtLengthValue = findViewById(R.id.txtLengthValue);
        checkboxUppercase = findViewById(R.id.checkboxUppercase);
        checkboxLowercase = findViewById(R.id.checkboxLowercase);
        checkboxNumbers = findViewById(R.id.checkboxNumbers);
        checkboxSpecialChars = findViewById(R.id.checkboxSpecialChars);
        Button buttonGeneratePassword = findViewById(R.id.buttonGeneratePassword);
        txtGeneratedPassword = findViewById(R.id.txtGeneratedPassword);
        Button buttonCopyPassword = findViewById(R.id.buttonCopyPassword);
        Button buttonSavePassword = findViewById(R.id.buttonSavePassword);

        mAuth = FirebaseAuth.getInstance();

        passwordStrengthMeter = findViewById(R.id.passwordStrengthMeter);

        txtLengthValue.setText(String.valueOf(seekBarLength.getProgress()));

        seekBarLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtLengthValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        txtLengthValue.setOnEditorActionListener((v, actionId, event) -> {
            try {
                int length = Integer.parseInt(txtLengthValue.getText().toString());
                if (length >= seekBarLength.getMin() && length <= seekBarLength.getMax()) {
                    seekBarLength.setProgress(length);
                } else {
                    Toast.makeText(GeneratePasswordScreen.this, "Please enter a valid length between " + seekBarLength.getMin() + " and " + seekBarLength.getMax(), Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(GeneratePasswordScreen.this, "Invalid length entered", Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        buttonGeneratePassword.setOnClickListener(v -> {
            int length = seekBarLength.getProgress();
            if (length < 1) {
                Toast.makeText(GeneratePasswordScreen.this, "Please select a length greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean includeUppercase = checkboxUppercase.isChecked();
            boolean includeLowercase = checkboxLowercase.isChecked();
            boolean includeNumbers = checkboxNumbers.isChecked();
            boolean includeSpecialChars = checkboxSpecialChars.isChecked();

            if (!includeUppercase && !includeLowercase && !includeNumbers && !includeSpecialChars) {
                Toast.makeText(GeneratePasswordScreen.this, "Please select at least one character type", Toast.LENGTH_SHORT).show();
                return;
            }

            String password = generatePassword(length, includeUppercase, includeLowercase, includeNumbers, includeSpecialChars);
            txtGeneratedPassword.setText(password);

            updatePasswordStrengthMeter(password);
        });

        buttonCopyPassword.setOnClickListener(v -> {
            String passwordToCopy = txtGeneratedPassword.getText().toString();

            if (!TextUtils.isEmpty(passwordToCopy)) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Password", passwordToCopy);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(GeneratePasswordScreen.this, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GeneratePasswordScreen.this, "No password to copy", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSavePassword.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                String userId = currentUser.getUid();
                String password = txtGeneratedPassword.getText().toString();
                if (!TextUtils.isEmpty(password)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Save Password Details");

                    final EditText inputName = new EditText(this);
                    inputName.setHint("Enter website name");

                    final EditText inputLink = new EditText(this);
                    inputLink.setHint("Enter website link");

                    LinearLayout layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.addView(inputName);
                    layout.addView(inputLink);
                    builder.setView(layout);

                    builder.setPositiveButton("Save", (dialog, which) -> {
                        String websiteName = inputName.getText().toString();
                        String websiteLink = inputLink.getText().toString();

                        savePasswordToFirestore(currentUser.getUid(), password, websiteName, websiteLink);
                    });

                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                    builder.show();

                } else {
                    Toast.makeText(getApplicationContext(), "No password to save", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updatePasswordStrengthMeter(String password) {
        int strengthPercentage = calculatePasswordStrength(password);
        passwordStrengthMeter.setProgress(strengthPercentage);

        if (strengthPercentage < 40) {
            passwordStrengthMeter.getProgressDrawable().setColorFilter(
                    Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (strengthPercentage < 70) {
            passwordStrengthMeter.getProgressDrawable().setColorFilter(
                    Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            passwordStrengthMeter.getProgressDrawable().setColorFilter(
                    Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private int calculatePasswordStrength(String password) {
        int lengthScore = Math.min(password.length() * 5, 30);
        int varietyScore = 0;

        if (password.matches(".*[A-Z].*")) varietyScore += 15;
        if (password.matches(".*[a-z].*")) varietyScore += 15;
        if (password.matches(".*\\d.*")) varietyScore += 20;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*"))
            varietyScore += 20;

        int totalScore = lengthScore + varietyScore;
        return Math.min(totalScore, 100);
    }

    private String generatePassword(int length, boolean includeUppercase, boolean includeLowercase, boolean includeNumbers, boolean includeSpecialChars) {
        String uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercaseChars = "abcdefghijklmnopqrstuvwxyz";
        String numberChars = "0123456789";
        String specialChars = "!@#$%^&*()_+[]{}|;:,.<>?/";

        StringBuilder charPool = new StringBuilder();
        if (includeUppercase) charPool.append(uppercaseChars);
        if (includeLowercase) charPool.append(lowercaseChars);
        if (includeNumbers) charPool.append(numberChars);
        if (includeSpecialChars) charPool.append(specialChars);

        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charPool.length());
            password.append(charPool.charAt(index));
        }

        return password.toString();
    }

    private void savePasswordToFirestore(String userId, String password, String websiteName, String websiteLink) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        // Create a map to store the password along with website name and link
        Map<String, Object> passwordDetails = new HashMap<>();
        passwordDetails.put("password", password);
        passwordDetails.put("websiteName", websiteName);
        passwordDetails.put("websiteLink", websiteLink);

        userRef.update("passwords", FieldValue.arrayUnion(passwordDetails))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(GeneratePasswordScreen.this, "Password saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Map<String, Object> initialData = new HashMap<>();
                    initialData.put("passwords", List.of(passwordDetails));

                    // If the arrayUnion fails, create the "passwords" field
                    userRef.set(initialData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(GeneratePasswordScreen.this, "Password saved successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(error -> {
                                Toast.makeText(GeneratePasswordScreen.this, "Failed to save password", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Error writing document", error);
                            });
                });
    }

}