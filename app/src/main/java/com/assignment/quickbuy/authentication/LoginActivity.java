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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        TextView buttonSkipLogin = findViewById(R.id.buttonSkipLogin);
        TextView textViewRegister = findViewById(R.id.textViewRegister);
        TextView forgetPass = findViewById(R.id.forgetPass);

        buttonLogin.setOnClickListener(v -> loginUser());

        buttonSkipLogin.setOnClickListener(v -> skipLogin());

        textViewRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            Animations.animateSwipeRight(LoginActivity.this);
            finish();
        });
        forgetPass.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            Animations.animateCard(LoginActivity.this);
            finish();
        });
    }

    private void loginUser() {
        String email = Objects.requireNonNull(editTextEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString().trim();

        if (email.isEmpty()) {
            ((TextInputLayout) findViewById(R.id.emailTextInputLayout)).setError("Email cannot be empty");
            return;
        } else {
            ((TextInputLayout) findViewById(R.id.emailTextInputLayout)).setError(null);
            if (!isValidEmail(email)) {
                ((TextInputLayout) findViewById(R.id.emailTextInputLayout)).setError("Invalid email address");
                return;
            } else {
                ((TextInputLayout) findViewById(R.id.emailTextInputLayout)).setError(null);
            }
        }

        if (password.isEmpty()) {
            ((TextInputLayout) findViewById(R.id.passwordTextInputLayout)).setError("Password cannot be empty");
            return;
        } else {
            ((TextInputLayout) findViewById(R.id.passwordTextInputLayout)).setError(null);
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                        saveUserId(uid);
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        Animations.animateZoom(LoginActivity.this);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserId(String uid) {
        SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_uid", uid);
        editor.apply();
    }

    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void skipLogin() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        Animations.animateFade(LoginActivity.this);
        finish();
    }
}
