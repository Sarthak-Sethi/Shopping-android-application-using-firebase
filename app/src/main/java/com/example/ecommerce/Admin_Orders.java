package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerce.Model.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Admin_Orders extends AppCompatActivity {
    RecyclerView orderslist;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__orders);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Orders");
        orderslist=findViewById(R.id.orders_list);
        orderslist.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options=new FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(databaseReference,AdminOrders.class).build();
        FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder> adapter=new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrderViewHolder holder, final int position, @NonNull AdminOrders model) {
                holder.uname.setText("Name :"+model.getName());
                holder.uphonenumber.setText("Phone :"+model.getPhone());
                holder.utotalprice.setText("Total Amount :"+model.getTotalAmount());
                holder.utime.setText("Order at : :"+model.getDate()+" "+model.getTime());
                holder.uaddress.setText("Shipping Adress :"+model.getAddress()+", "+model.getCity());
                holder.showorderbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uID=getRef(position).getKey();
                        Intent intent=new Intent(Admin_Orders.this,AdminUserProduct.class);
                        intent.putExtra("uid",uID);
                        startActivity(intent);
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Yes",
                                        "No"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(Admin_Orders.this);
                        builder.setTitle("Have you shipped the Products ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0)
                                {
                                    String uID=getRef(position).getKey();
                                    removeOrder(uID);
                                }
                                if(which==1)
                                {
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            private void removeOrder(String uID) {
                databaseReference.child(uID).removeValue();
            }

            @NonNull
            @Override
            public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                AdminOrderViewHolder holder=new AdminOrderViewHolder(view);
                return holder;
            }
        };
        orderslist.setAdapter(adapter);
        adapter.startListening();
    }
}
