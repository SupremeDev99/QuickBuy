package com.assignment.quickbuy.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalBtn, msgTxt;
    private DatabaseReference cartListRef;
    private Double overallTotalPrice;
    private List<String> cartProductIDs;
    private SharedPreferences sharedPreferences;
    private FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;
    private LottieAnimationView nothing;

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
        nothing = view.findViewById(R.id.nothing);

        cartProductIDs = new ArrayList<>();

        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmFinalOrderActivity(String.valueOf(overallTotalPrice));
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        overallTotalPrice = 0.0;
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

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View").child(userUID).child("Products"), snapshot -> {
                    Cart cart = snapshot.getValue(Cart.class);
                    // Handle null values or other custom logic if needed
                    return cart;
                })
                .build();

        adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                try {
                    double itemPrice = model.getPrice() * model.getQuantity();
                    overallTotalPrice += itemPrice;
                } catch (NumberFormatException e) {
                    Log.e("CartFragment", "Error parsing price: ", e);
                }

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                txtTotalBtn.setText("Total Price: $" + decimalFormat.format(overallTotalPrice));

                holder.txtProductName.setText(model.getPname());
                holder.txtProductQuantity.setText(String.valueOf(model.getQuantity())); // Convert to String
                holder.txtProductPrice.setText(decimalFormat.format(model.getPrice()) + "$"); // Convert to String

                Picasso.get().load(model.getImage()).into(holder.imgProductImage);

                holder.itemView.setOnClickListener(v -> {
                    loadProductDetailsAndOpenActivity(model.getPid());
                });

                // Handle edit button click
                holder.vRectangle_edit.setOnClickListener(v -> {
                    showQuantityDialog(model.getPid(), model.getQuantity());
                });

                // Handle delete button click
                holder.vRectangle_delete.setOnClickListener(v -> {
                    // Implement your logic to delete the item from Firebase
                    deleteItemFromFirebase(model.getPid());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("ordered", false);
                    editor.apply();
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new CartViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items_layout, viewGroup, false));
            }
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0) {
                    handleNoCartItems();
                }else {
                    handleCartItems();
                }
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private void deleteItemFromFirebase(String productID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this item from your cart?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            String userUID = sharedPreferences.getString("user_uid", "");

            DatabaseReference userCartRef = cartListRef.child("User View").child(userUID).child("Products");
            DatabaseReference productRef = userCartRef.child(productID);

            productRef.removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Item deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // Do nothing if the user cancels the delete operation
        });

        builder.show();
    }


    private void showQuantityDialog(String productID, int currentQuantity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Quantity");

        // Create a number picker for selecting the quantity
        NumberPicker numberPicker = new NumberPicker(requireContext());
        numberPicker.setMinValue(1); // Minimum quantity
        numberPicker.setMaxValue(10); // Maximum quantity
        numberPicker.setValue(currentQuantity); // Default quantity

        builder.setView(numberPicker);

        builder.setPositiveButton("Update Quantity", (dialog, which) -> {
            int selectedQuantity = numberPicker.getValue();
            updateQuantityInFirebase(productID, selectedQuantity);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Do nothing or handle cancellation if needed
        });

        builder.show();
    }

    private void updateQuantityInFirebase(String productID, int newQuantity) {
        String userUID = sharedPreferences.getString("user_uid", "");

        DatabaseReference userCartRef = cartListRef.child("User View").child(userUID).child("Products");
        DatabaseReference productRef = userCartRef.child(productID);

        productRef.child("quantity").setValue(newQuantity)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Quantity updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update quantity", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void handleNoCartItems() {
        if (sharedPreferences.getBoolean("ordered", false)) {
            msgTxt.setVisibility(View.VISIBLE);
            nothing.setVisibility(View.GONE);
        }else {
            nothing.setVisibility(View.VISIBLE);
        }
    }
    private void handleCartItems() {

        msgTxt.setVisibility(View.GONE);
        nothing.setVisibility(View.GONE);
    }

}
