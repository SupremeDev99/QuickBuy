package com.assignment.quickbuy.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.assignment.quickbuy.Animations;
import com.assignment.quickbuy.MainActivity;
import com.assignment.quickbuy.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword, confirmPass;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        confirmPass = findViewById(R.id.confirmPass);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        TextView textViewLogin = findViewById(R.id.textViewLogin);
        TextView skip = findViewById(R.id.buttonSkipReg);

        buttonRegister.setOnClickListener(v -> registerUser());

        textViewLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            Animations.animateSwipeLeft(RegisterActivity.this);
            finish();
        });
        skip.setOnClickListener(v -> skipLogin());
    }

    private void registerUser() {
        String email = Objects.requireNonNull(editTextEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(confirmPass.getText()).toString().trim();

        if (validateInput(email, password, confirmPassword)) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                            saveUserId(uid);
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            Animations.animateZoom(RegisterActivity.this);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateInput(String email, String password, String confirmPassword) {
        if (password.isEmpty()) {
            ((TextInputLayout) findViewById(R.id.passwordTextInputLayout)).setError("Password cannot be empty");
            return false;
        } else {
            ((TextInputLayout) findViewById(R.id.passwordTextInputLayout)).setError(null);
            if (!isValidEmail(email)) {
                ((TextInputLayout) findViewById(R.id.emailTextInputLayout)).setError("Invalid email address");
                return false;
            } else {
                ((TextInputLayout) findViewById(R.id.emailTextInputLayout)).setError(null);
            }
        }

        if (!password.equals(confirmPassword)) {
            ((TextInputLayout) findViewById(R.id.confirmPasswordTextInputLayout)).setError("Passwords do not match");
            return false;
        } else {
            ((TextInputLayout) findViewById(R.id.confirmPasswordTextInputLayout)).setError(null);
        }

        return true;
    }

    private void saveUserId(String uid) {
        SharedPreferences sharedPreferences = RegisterActivity.this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_uid", uid);
        editor.apply();
    }

    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void skipLogin() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        Animations.animateFade(RegisterActivity.this);
        finish();
    }
}
