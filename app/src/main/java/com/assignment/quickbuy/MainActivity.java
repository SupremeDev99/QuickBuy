package com.assignment.quickbuy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.assignment.quickbuy.adapters.ViewPagerAdapter;
import com.assignment.quickbuy.authentication.LoginActivity;
import com.assignment.quickbuy.fragment.CartFragment;
import com.assignment.quickbuy.fragment.HomeFragment;
import com.assignment.quickbuy.fragment.ProfileFragment;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    BubbleToggleView l_item_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isNetworkAvailable()) {
            showNoInternetDialog();
        }
        FirebaseApp.initializeApp(this);
        viewPager = findViewById(R.id.view_pager);
        l_item_cart = findViewById(R.id.l_item_cart);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new HomeFragment());
        viewPagerAdapter.addFragment(new CartFragment());
        viewPagerAdapter.addFragment(new ProfileFragment());

        viewPager.setAdapter(viewPagerAdapter);
        final BubbleNavigationLinearView bubbleNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);
        bubbleNavigationLinearView.setTypeface(Typeface.createFromAsset(getAssets(), "rubik.ttf"));
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        bubbleNavigationLinearView.setBadgeValue(0, null);
        bubbleNavigationLinearView.setBadgeValue(1, null);
        if (currentUser != null){
            bubbleNavigationLinearView.setBadgeValue(2, null);
        }else {
            bubbleNavigationLinearView.setBadgeValue(2, "");
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
                if (isNetworkAvailable()) {
                    if (currentUser != null) {
                        startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                        Animations.animateSlideUp(MainActivity.this);
                    } else {
                        Toast.makeText(this, "Please login first!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                } else {
                    showNoInternetDialog();
                }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                bubbleNavigationLinearView.setCurrentActiveItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        bubbleNavigationLinearView.setNavigationChangeListener((view, position) -> viewPager.setCurrentItem(position, true));
        if (isFirstLaunch()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            setFirstLaunch(false);
        }
    }

    private boolean isFirstLaunch() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("firstLaunch", true);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }
        return false;
    }
    public void setFabVisibility(int visibility) {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(visibility);
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_no_internet, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button btnRetry = dialogView.findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Still no internet", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    private void setFirstLaunch(boolean isFirstLaunch) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstLaunch", isFirstLaunch);
        editor.apply();
    }
}
