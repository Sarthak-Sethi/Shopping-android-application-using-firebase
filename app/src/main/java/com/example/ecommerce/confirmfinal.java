package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class confirmfinal extends AppCompatActivity {
    EditText uname,uphonenumber,uaddress,ucity;
    Button confirmOrderBtn;
    String totalAmount="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmfinal);
        totalAmount=getIntent().getStringExtra("Total Price");
        Toast.makeText(this,"Total Price ="+totalAmount,Toast.LENGTH_LONG).show();
        confirmOrderBtn=(Button)findViewById(R.id.confirm_final_order_btn);
        uname=(EditText)findViewById(R.id.shipment_name);
        uphonenumber=(EditText)findViewById(R.id.shipment_phonenumber);
        uaddress=(EditText)findViewById(R.id.shipment_address);
        ucity=(EditText)findViewById(R.id.shipment_city);
        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }
    public void check()
    {
        if(TextUtils.isEmpty(uname.getText().toString()))
        {
           uname.setError("please Provide your name");
        }
        else if(TextUtils.isEmpty(uphonenumber.getText().toString()))
        {
       uphonenumber.setError("Please Provide your phone number");
        }
        else if(TextUtils.isEmpty(uaddress.getText().toString()))
        {
            uaddress.setError("Please Provide your Address");
        }
        if(TextUtils.isEmpty(ucity.getText().toString()))
        {
            ucity.setError("Please Provide City");
        }
        else
        {
            ConfirmOrder();
        }

    }
    public void ConfirmOrder() {
        final String saveCurrentDate, saveCurrentTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        final DatabaseReference ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalant.currentOnlineUser.getPhone());
        HashMap<String,Object> map=new HashMap<>();
        map.put("totalAmount",totalAmount);
        map.put("name",uname.getText().toString());
        map.put("phone",uphonenumber.getText().toString());
        map.put("address",uaddress.getText().toString());
        map.put("city",ucity.getText().toString());
        map.put("date",saveCurrentDate);
        map.put("time",saveCurrentTime);
        map.put("state","Not Shipped");
        ordersRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalant.currentOnlineUser.getPhone()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(confirmfinal.this,"Your order has been placed successfully",Toast.LENGTH_LONG).show();
                                        Intent intent=new Intent(confirmfinal.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }

}
