package com.assignment.quickbuy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.assignment.quickbuy.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText etxtFullName ,etxtPhoneNumber,etxtHomeAddress,etxtCityName;
    private Button shippmentBackbtn,shippmentConfirmBtn;
    private String totalAmount="";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        totalAmount=getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price is : "+totalAmount+"$", Toast.LENGTH_SHORT).show();
        etxtFullName=(EditText)findViewById(R.id.shippment_name);
        etxtPhoneNumber=(EditText)findViewById(R.id.shippment_phone);
        etxtHomeAddress=(EditText)findViewById(R.id.shippment_home_address);
        etxtCityName=(EditText)findViewById(R.id.shippment_city_name);
        shippmentBackbtn=(Button)findViewById(R.id.shippment_back_btn);
        shippmentConfirmBtn=(Button)findViewById(R.id.shippment_confirm_btn);

        shippmentBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shippmentConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

    }

    private void check()
    {
        if(TextUtils.isEmpty(etxtFullName.getText().toString()))
        {
            Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show();
        }
      else if(TextUtils.isEmpty(etxtPhoneNumber.getText().toString()))
        {
            Toast.makeText(this, "Phone number is Empty", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(etxtHomeAddress.getText().toString()))
        {
            Toast.makeText(this, "Home Address is Empty", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(etxtCityName.getText().toString()))
        {
            Toast.makeText(this, "City Name is Empty", Toast.LENGTH_SHORT).show();
        }
        else
        {
            confirmOrder();
        }

    }

    private void confirmOrder() {
        removeCartItems();
        Intent intent = new Intent(ConfirmFinalOrderActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void removeCartItems() {
        DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        String userUID = sharedPreferences.getString("user_uid", "");
        SharedPreferences.Editor editor = sharedPreferences.edit();

        cartListRef.child("User View").child(userUID).child("Products").removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ConfirmFinalOrderActivity.this, "Order will be Placed", Toast.LENGTH_SHORT).show();
                            editor.putBoolean("ordered", true);
                            editor.apply();

                        } else {
                            Toast.makeText(ConfirmFinalOrderActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
