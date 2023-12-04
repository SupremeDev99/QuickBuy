package com.assignment.quickbuy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.assignment.quickbuy.authentication.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Check if it's the first launch
        if (true) {
            startActivity(new Intent(this, LoginActivity.class));
            setFirstLaunch(false);
            finish();
        }
    }

    // Check if it's the first launch
    private boolean isFirstLaunch() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("firstLaunch", true);
    }

    // Set the first launch flag
    private void setFirstLaunch(boolean isFirstLaunch) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstLaunch", isFirstLaunch);
        editor.apply();
    }

    // Example method to check if the user is already authenticated
    private boolean userIsAuthenticated() {
        // Implement your authentication check here
        // For example, check if the user is logged in through Firebase Auth
        // You can use FirebaseAuth.getInstance().getCurrentUser() != null
        // or any other authentication mechanism
        return false;
    }
}
