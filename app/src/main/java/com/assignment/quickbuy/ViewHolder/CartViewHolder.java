package com.assignment.quickbuy.ViewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.assignment.quickbuy.Interface.ItemClickListner;
import com.assignment.quickbuy.R;

public class CartViewHolder extends RecyclerView.ViewHolder {

    public TextView txtProductName;
    public TextView txtProductQuantity;
    public TextView txtProductPrice;
    public TextView txtProductTotalPrice;
    public ImageView imgProductImage;
    public View vRectangle_edit;
    public View vRectangle_delete;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.cart_view_product_name);
        txtProductQuantity = itemView.findViewById(R.id.cart_view_show_quantity);
        txtProductPrice = itemView.findViewById(R.id.cart_view_price);
        txtProductTotalPrice = itemView.findViewById(R.id.total_price_txt);
        imgProductImage = itemView.findViewById(R.id.cart_view_product_image);
        vRectangle_edit = itemView.findViewById(R.id.rectangle_for_edit);
        vRectangle_delete = itemView.findViewById(R.id.rectangle_for_delete);
    }
}


