package com.example.ecommerce;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminOrderViewHolder extends RecyclerView.ViewHolder
{
    public TextView uname,uphonenumber,utotalprice,utime,uaddress;
    Button showorderbtn;

    public AdminOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        uname= itemView.findViewById(R.id.order_user_name);
        uphonenumber= itemView.findViewById(R.id.order_phone_number);
        utotalprice= itemView.findViewById(R.id.order_total_price);
        utime= itemView.findViewById(R.id.order_date_time);
        uaddress= itemView.findViewById(R.id.order_address_city);
        showorderbtn = itemView.findViewById(R.id.show_all_products_btn);

    }
}
