package com.example.ecommerce;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    public TextView txtProductName,txtProductDescription,txtProductPrice;
    public ImageView productImageView;


    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productImageView = itemView.findViewById(R.id.productImageView);
        txtProductName = itemView.findViewById(R.id.product_name);
        txtProductPrice = itemView.findViewById(R.id.product_price);
        txtProductDescription = itemView.findViewById(R.id.product_description);
    }
}
