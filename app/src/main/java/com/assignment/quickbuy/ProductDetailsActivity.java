package com.assignment.quickbuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.assignment.quickbuy.Model.Products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productPrice,productDescription,productName;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        String productID = getIntent().getStringExtra("pid");

        Button addToCartBtn = findViewById(R.id.add_product_to_cart_btn);
        productImage= findViewById(R.id.product_image_details);
        productName= findViewById(R.id.product_name_details);
        productDescription= findViewById(R.id.product_description_details);
        productPrice= findViewById(R.id.product_price_details);


        getProductDetails(productID);

        addToCartBtn.setOnClickListener(v -> addingToCartList(productID));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void addingToCartList(String productID) {
        Set<String> cartIDs = new HashSet<>();

        String cartIDsString = sharedPreferences.getString("cart_ids", "");
        if (!cartIDsString.isEmpty()) {
            String[] cartIDArray = cartIDsString.split(",");
            cartIDs.addAll(Arrays.asList(cartIDArray));
        }

        boolean productExists = cartIDs.contains(productID);

        if (!productExists) {
            cartIDs.add(productID);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cart_ids", String.join(",", cartIDs));
            editor.apply();

            DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
            String userUID = sharedPreferences.getString("user_uid", "");
            cartListRef.child("User View").child(userUID).child("Products").child(productID).setValue("Added");

            updateCartBadge(cartIDs.size());

            Toast.makeText(this, "Product Added to Cart!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product already exists in cart!", Toast.LENGTH_SHORT).show();
        }
    }




    private void updateCartBadge(int cartSize) {

    }


    private void getProductDetails(String productID)
    {
        DatabaseReference productsRef= FirebaseDatabase.getInstance().getReference().child("Products");


        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    Products products =dataSnapshot.getValue(Products.class);
                    assert products != null;
                    productName.setText(products.getPname());
                    productDescription.setText(products.getDescription());
                    productPrice.setText(products.getPrice()+"$");
                    Picasso.get().load(products.getimage()).into(productImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
