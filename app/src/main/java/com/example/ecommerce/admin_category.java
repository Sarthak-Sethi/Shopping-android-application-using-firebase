package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class admin_category extends AppCompatActivity {
ImageView tshirts,sportstshirts,FemaleDresses,sweaters;
ImageView glasses,hats,walletbagspurses,shoes;
ImageView headphonehandfree,laptops,watches,mobilephones;
Button logoutbtn,checkorderbtn,maintainproductbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        tshirts=findViewById(R.id.ivTshirts);
        sportstshirts=findViewById(R.id.ivSportsTshirts);
        FemaleDresses=findViewById(R.id.ivFemaleDresses);
        sweaters=findViewById(R.id.ivSweathers);

        glasses=findViewById(R.id.ivGlasses);
        walletbagspurses=findViewById(R.id.ivPurses);
        hats=findViewById(R.id.ivHats);
        shoes=findViewById(R.id.ivShoes);

        headphonehandfree=findViewById(R.id.ivHeadphones);
        laptops=findViewById(R.id.ivLaptops);
        watches=findViewById(R.id.ivWatches);
        mobilephones=findViewById(R.id.ivSmartPhones);

        logoutbtn = findViewById(R.id.admin_logout_btn);
        checkorderbtn = findViewById(R.id.check_orders_btn);

        maintainproductbtn=(Button)findViewById(R.id.maintain_button);


        //listners
        tshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "T-Shirts");
                startActivity(intent);

            }
        });

        sportstshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "Sports T shirts");
                startActivity(intent);

            }
        });

        FemaleDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "Female Dresses");
                startActivity(intent);
            }
        });
        sweaters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "Sweaters");
                startActivity(intent);
            }
        });
        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "glasses");
                startActivity(intent);
            }
        });
        hats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "Hats");
                startActivity(intent);
            }
        });
        walletbagspurses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "Purses");
                startActivity(intent);
            }
        });
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "Shoes");
                startActivity(intent);
            }
        });
        headphonehandfree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "HeadPhones");
                startActivity(intent);
            }
        });
        mobilephones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "Mobile Phones");
                startActivity(intent);
            }
        });
        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "Laptops");
                startActivity(intent);
            }
        });

        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "Watches");
                startActivity(intent);
            }
        });
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        checkorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this,Admin_Orders.class);
                startActivity(intent);
            }
        });
        maintainproductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(admin_category.this,HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
            }
        });

    }//on create
}
