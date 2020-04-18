package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Model.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button nextbtn;
    TextView txttotalprice,txtmMg1;
    int totalprice=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView=findViewById(R.id.cart_list);
        txtmMg1 = findViewById(R.id.message);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        nextbtn=(Button)findViewById(R.id.next_process_btn);
        txttotalprice=(TextView)findViewById(R.id.total_price);
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txttotalprice.setText("Total Price ="+String.valueOf(totalprice));
                Intent i = new Intent(CartActivity.this,confirmfinal.class);
                i.putExtra("Total Price", String.valueOf(totalprice));
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options= new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef.child("User View").child(Prevalant.currentOnlineUser.getPhone()).child("Products"),Cart.class).build();

        FirebaseRecyclerAdapter<Cart,CardViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CardViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CardViewHolder holder, int position, @NonNull final Cart model) {
                holder.pquantity.setText("Quantity = "+model.getQuantity());
                holder.pname.setText(model.getPname());
                holder.pprice.setText("Price "+ model.getPrice());

                int totalfor1 = (Integer.valueOf(model.getPrice()))*Integer.valueOf(model.getQuantity());
                totalprice+=totalfor1;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "EDIT",
                                "REMOVE"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Title");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                           //which contians user has elected edit or remove
                                if(which==0)
                                {
                                    Intent intent=new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid()); // it gets only that product jo selected h already
                                    startActivity(intent);
                                }
                                if(which ==1){
                                    cartListRef.child("User View").child(Prevalant.currentOnlineUser.getPhone()).child("Products").child(model.getPid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this,"Item removed successfuly ",Toast.LENGTH_LONG).show();
                                                        Intent intent=new Intent(CartActivity.this,HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items,parent,false);
                CardViewHolder holder=new CardViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private void CheckOrderState() {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalant.currentOnlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Log.e("a","Sarthak 111111111");
                    String shippingState=dataSnapshot.child("state").getValue().toString();
                    String userName=dataSnapshot.child("name").getValue().toString();

                    if(shippingState.equals("shipped"))
                    {
                        txttotalprice.setText("Dear "+userName+"\n order is shipped successfully, you can purchase more products once your existing product is approved ");
                        recyclerView.setVisibility(View.GONE);
                        txtmMg1.setVisibility(View.VISIBLE);
                        txtmMg1.setText("Congratulations, your final order has been shipped and you will recieve at your door soon");
                        nextbtn.setVisibility(View.GONE);


                    }
                    else if(shippingState.equals("Not Shipped"))
                    {
                        txttotalprice.setText("Shipping State = Not Shipped ");
                        recyclerView.setVisibility(View.GONE);
                        txtmMg1.setVisibility(View.VISIBLE);
                        nextbtn.setVisibility(View.GONE);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
