package com.assignment.quickbuy.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.assignment.quickbuy.R;
import com.assignment.quickbuy.authentication.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private ImageView profileImageView;
    private TextView emailTextView;
    private EditText usernameEditText;
    private Button logoutButton;
    private Button deleteAccountButton;
    private Button addPaymentMethodButton;
    private Button helpButton;
    private Button saveUsernameButton;
    private TextView usernameTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        profileImageView = view.findViewById(R.id.animationView);
        emailTextView = view.findViewById(R.id.emailTextView);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        logoutButton = view.findViewById(R.id.logoutButton);
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton);
        addPaymentMethodButton = view.findViewById(R.id.addPaymentMethodButton);
        helpButton = view.findViewById(R.id.helpButton);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        saveUsernameButton = view.findViewById(R.id.saveUsernameButton);
        saveUsernameButton.setOnClickListener(v -> {
            saveUsernameToPreferences(usernameEditText.getText().toString());
            usernameTextView.setText(usernameEditText.getText().toString());
            saveUsernameButton.setVisibility(View.GONE);
            usernameEditText.setVisibility(View.GONE);
        });

        // Set user email (replace with actual Firebase user information)
        if (mAuth.getCurrentUser() != null) {
            String userEmail = mAuth.getCurrentUser().getEmail();
            emailTextView.setText(userEmail);
        }

        // Set existing username if available
        String savedUsername = getUsernameFromPreferences();
        if (!savedUsername.isEmpty()) {
            usernameTextView.setText(savedUsername);
            saveUsernameButton.setVisibility(View.GONE);
            usernameEditText.setVisibility(View.GONE);

        }

        logoutButton.setOnClickListener(v -> confirmLogout());
        deleteAccountButton.setOnClickListener(v -> confirmDeleteAccount());
        addPaymentMethodButton.setOnClickListener(v -> addPaymentMethod());
        helpButton.setOnClickListener(v -> openWebsite("https://example.com"));

        return view;
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logoutUser())
                .setNegativeButton("No", null)
                .show();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action is irreversible.")
                .setPositiveButton("Yes", (dialog, which) -> deleteAccount())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAccount() {
        mAuth.getCurrentUser().delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Your account successfully deleted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Error with delete account!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addPaymentMethod() {
        showPaymentDialog();
    }

    private void showPaymentDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Add Payment Method")
                .setMessage("Enter your payment information:")
                .setPositiveButton("Add", (dialog, which) -> {
                    Toast.makeText(requireContext(), "Payment method added!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openWebsite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void saveUsernameToPreferences(String username) {
        requireContext().getSharedPreferences("MyPrefs", 0).edit().putString("username", username).apply();
    }

    private String getUsernameFromPreferences() {
        return requireContext().getSharedPreferences("MyPrefs", 0).getString("username", "");
    }
}
