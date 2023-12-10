package com.assignment.quickbuy.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.assignment.quickbuy.ConfirmFinalOrderActivity;
import com.assignment.quickbuy.MainActivity;
import com.assignment.quickbuy.Model.Cart;
import com.assignment.quickbuy.Model.Products;
import com.assignment.quickbuy.ProductDetailsActivity;
import com.assignment.quickbuy.R;
import com.assignment.quickbuy.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalBtn, msgTxt;
    private DatabaseReference cartListRef;
    private int overallTotalPrice;
    private List<String> cartProductIDs;
    private SharedPreferences sharedPreferences;
    private FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);

        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        recyclerView = view.findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        Context context = requireActivity();
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        nextProcessBtn = view.findViewById(R.id.next_btn);
        txtTotalBtn = view.findViewById(R.id.total_price_txt);
        msgTxt = view.findViewById(R.id.msgtxt);
        msgTxt.setVisibility(View.GONE);

        cartProductIDs = new ArrayList<>();

        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCartData();

                openConfirmFinalOrderActivity(String.valueOf(overallTotalPrice));
            }
        });

        return view;
    }

    private void clearCartData() {
        cartListRef.removeValue();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("cart_ids").apply();

        cartProductIDs.clear();
        adapter.notifyDataSetChanged();

        overallTotalPrice = 0;
        txtTotalBtn.setText("Total Price: $0");
    }


    @Override
    public void onStart() {
        super.onStart();
        checkOrderState();
        loadCartProductIDs();
        displayCartItems();
    }

    private void checkOrderState() {
    }

    private void loadCartProductIDs() {
        String cartIDs = sharedPreferences.getString("cart_ids", "");

        cartProductIDs = new ArrayList<>();
        String[] cartIDArray = cartIDs.split(",");
        for (String cartID : cartIDArray) {
            cartProductIDs.add(cartID);
        }

        Log.d("CartFragment", "Cart Product IDs: " + cartProductIDs.toString());
    }


    private String getCartID(int index) {
        String cartIDs = sharedPreferences.getString("cart_ids", "");
        String[] cartIDArray = cartIDs.split(",");

        if (index >= 0 && index < cartIDArray.length) {
            return cartIDArray[index];
        } else {
            return "";
        }
    }

    private void loadProductDetailsAndOpenActivity(String productID) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Products products = dataSnapshot.getValue(Products.class);
                    if (products != null) {
                        openProductDetailsActivity(products);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void openProductDetailsActivity(Products products) {
        Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
        intent.putExtra("pid", products.getPid());
        startActivity(intent);
    }

    private void openConfirmFinalOrderActivity(String totalPrice) {
        Intent intent = new Intent(requireContext(), ConfirmFinalOrderActivity.class);
        intent.putExtra("Total Price", totalPrice);
        startActivity(intent);
    }

    private void displayCartItems() {
        String userUID = sharedPreferences.getString("user_uid", "");

        adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View").child(userUID).child("Products"), Cart.class)
                        .build()
        ) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new CartViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items_layout, viewGroup, false));
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
