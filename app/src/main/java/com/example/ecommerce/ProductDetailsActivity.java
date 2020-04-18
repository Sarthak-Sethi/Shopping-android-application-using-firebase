package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecommerce.Model.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
    Button CartBtn;
    ImageView productImage;
    ElegantNumberButton itemcountbtn;
    TextView pprice,pdescription,pname;
    String productId="",state = "Normal";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        CartBtn=(Button) findViewById(R.id.addtocartBtn);
        productImage=(ImageView)findViewById(R.id.product_image_details);
        pprice=(TextView)findViewById(R.id.product_price_details);
        pdescription=(TextView)findViewById(R.id.product_description_details);
        pname=(TextView)findViewById(R.id.product_name_details);
        itemcountbtn=(ElegantNumberButton)findViewById(R.id.number_btn);
        productId=getIntent().getStringExtra("pid");
        getproductdetails(productId);
        CartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state.equals("Order Placed") || state.equals("Order Shipped"))
                {
                    Toast.makeText(ProductDetailsActivity.this,"YOU can purchase more products once your previous order is confirmed",Toast.LENGTH_LONG).show();

                }else{
                    AddingToCartList();
                }

            }
        });
    }
    protected void onStart() {
        super.onStart();
        CheckOrderState();
    }

    public void AddingToCartList() {
        final String saveCurrentTime,saveCurrentDate;
        Calendar calForDate= Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calForDate.getTime());
        DatabaseReference productsRef=FirebaseDatabase.getInstance().getReference().child("Products");
        productsRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
                final HashMap<String,Object> cartMap=new HashMap<>();
                cartMap.put("pid",productId);
                cartMap.put("pname",pname.getText().toString());
                cartMap.put("price",pprice.getText().toString());
                cartMap.put("date",saveCurrentDate);
                cartMap.put("time",saveCurrentTime);
                cartMap.put("quantity",itemcountbtn.getNumber());
                cartMap.put("discount","");
                cartListRef.child("User View").child(Prevalant.currentOnlineUser.getPhone()).child("Products").child(productId).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()) {
                       cartListRef.child("Admin View").child(Prevalant.currentOnlineUser.getPhone()).child("Products").child(productId).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful()){
                                   Toast.makeText(ProductDetailsActivity.this,"ADDED TO CART",Toast.LENGTH_LONG).show();
                                   Intent i  = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                   startActivity(i);
                               }
                           }
                       });

                       }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getproductdetails(String productId)
    {
        DatabaseReference productsRef= FirebaseDatabase.getInstance().getReference().child("Products");
        productsRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Products products=dataSnapshot.getValue(Products.class);
                    pname.setText(products.getPname());
                    pprice.setText(products.getPrice());
                    pdescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage_url()).into(productImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void CheckOrderState() {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalant.currentOnlineUser.getPhone());
Log.e("A","Order in check order");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    if (shippingState.equals("shipped")) {
                        state = "Order Shipped";
                    } else if (shippingState.equals("Not Shipped")) {
                        state = "Order Placed";
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
