package com.assignment.quickbuy.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.assignment.quickbuy.Animations;
import com.assignment.quickbuy.MainActivity;
import com.assignment.quickbuy.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        Button buttonResetPassword = findViewById(R.id.buttonResetPassword);
        TextView textViewLogin = findViewById(R.id.textViewLogin);
        TextView skip = findViewById(R.id.buttonSkipLogin);

        buttonResetPassword.setOnClickListener(v -> resetPassword());
        skip.setOnClickListener(v -> skipLogin());

        textViewLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            Animations.animateDiagonal(ForgotPasswordActivity.this);
            finish();
        });
    }

    private void resetPassword() {
        String email = editTextEmail.getText().toString().trim();
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

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Snackbar.make(findViewById(android.R.id.content), "Password reset email sent", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void skipLogin() {
        startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
        Animations.animateFade(ForgotPasswordActivity.this);
        finish();
    }

}
