package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerce.Model.Cart;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminUserProduct extends AppCompatActivity {
    RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference;
    String userid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_product);
        productsList=findViewById(R.id.products_list);
        productsList.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);
        userid=getIntent().getStringExtra("uid");
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View")
                .child(userid).child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options=new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(databaseReference,Cart.class)
                .build();
        FirebaseRecyclerAdapter<Cart, CardViewHolder> adapter=new FirebaseRecyclerAdapter<Cart, CardViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CardViewHolder holder, int position, @NonNull Cart model) {
                holder.pquantity.setText("Quantity = "+model.getQuantity());
                holder.pname.setText(model.getPname());
                holder.pprice.setText("Price "+model.getPrice());
            }

            @NonNull
            @Override
            public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items,parent,false);
                CardViewHolder holder=new CardViewHolder(view);
                return holder;
            }
        };
        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}
