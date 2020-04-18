package com.example.ecommerce;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CardViewHolder extends RecyclerView.ViewHolder  {
    public TextView pname,pprice,pquantity;

    public CardViewHolder(View view){
        super(view);
        pprice=itemView.findViewById(R.id.cart_product_price);
        pname=itemView.findViewById(R.id.cart_product_name);
        pquantity=itemView.findViewById(R.id.cart_product_quantity);
    }
}
