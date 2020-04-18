package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProduct extends AppCompatActivity {
    Button applychangesbtn,deletebtn;
    EditText pname,pprice,pdescription;
    ImageView imageView;
    String productId="";
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);
        productId=getIntent().getStringExtra("pid");

        applychangesbtn=(Button)findViewById(R.id.apply_changes_btn);
        pname=(EditText)findViewById(R.id.product_name_maintain);
        pprice=(EditText)findViewById(R.id.product_price_maintain);
        pdescription=(EditText)findViewById(R.id.product_description_maintain);
        imageView=(ImageView)findViewById(R.id.product_image_maintain);
        deletebtn = findViewById(R.id.delete_product_btn);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        displaySpecificProductInfo();

        applychangesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletethebutton();
            }
        });
    }

    private void deletethebutton() {
        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent=new Intent(AdminMaintainProduct.this,admin_category.class);
                startActivity(intent);
                Toast.makeText(AdminMaintainProduct.this,"Product Deleted ",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void applyChanges() {
        String pName=pname.getText().toString();
        String pPrice=pprice.getText().toString();
        String pDescription=pdescription.getText().toString();
        if(pName.equals(""))
        {
            Toast.makeText(this,"Write down name",Toast.LENGTH_LONG).show();
        }
        else if(pPrice.equals(""))
        {
            Toast.makeText(this,"Write down price",Toast.LENGTH_LONG).show();
        }
        else if(pDescription.equals(""))
        {
            Toast.makeText(this,"Write down description",Toast.LENGTH_LONG).show();
        }
        else
        {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productId);
            productMap.put("description", pDescription);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);
            databaseReference.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(AdminMaintainProduct.this,"Changes applied successfully ",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(AdminMaintainProduct.this,admin_category.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    public void displaySpecificProductInfo() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String name=dataSnapshot.child("pname").getValue().toString();
                    String price=dataSnapshot.child("price").getValue().toString();
                    String description=dataSnapshot.child("description").getValue().toString();
                    String pImage=dataSnapshot.child("image").getValue().toString();
                    pname.setText(name);
                    pprice.setText(price);
                    pdescription.setText(description);
                    Picasso.get().load(pImage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
