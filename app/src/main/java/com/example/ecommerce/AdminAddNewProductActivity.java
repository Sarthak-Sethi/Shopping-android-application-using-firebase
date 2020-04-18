package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
ImageView selectProductImage;
String CategoryName,Descrpition,Price,Pname,SaveCurrentDate,SaveCurrentTime;
Button AddNewProduct;
EditText InputProductName,InputProductDescrption,InputProductPrice;
public static final int GALLERYPICK=1;//key passed jab image leke aaye gallery se it is checked ki same h naa
    public Uri imageUri;
    String ProductRandomKey,downloadImageUrl;
    ProgressDialog loadingBar;
    private StorageReference productImagesRef;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        loadingBar= new ProgressDialog(this);

        CategoryName = getIntent().getExtras().get("category").toString();

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");

        AddNewProduct=findViewById(R.id.add_new_product);
        InputProductName = findViewById(R.id.product_name);
        InputProductDescrption = findViewById(R.id.product_description);
        InputProductPrice = findViewById(R.id.product_price);
        selectProductImage = findViewById(R.id.select_product_image);
        selectProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        AddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

    }

    private void ValidateProductData() {
        Descrpition = InputProductDescrption.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();
        if(imageUri==null)
        {
            Toast.makeText(this,"Product Image is Mandatory",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(Descrpition))
        {
            Toast.makeText(this,"Please write product description",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(Price))
        {
            Toast.makeText(this,"Please write product Price",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(Pname))
        {
            Toast.makeText(this,"Please write product Name",Toast.LENGTH_LONG).show();
        }
        else
        {
            StoreProductInformation();
        }

    }

    private void StoreProductInformation() {
        loadingBar.setTitle("Add New product");
        loadingBar.setMessage("Dear Admin,Please wait while we are adding the Product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd,yyyy");
        SaveCurrentDate = currentdate.format(calendar.getTime());

        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss a"); // a is for am or pm
        SaveCurrentTime = currenttime.format(calendar.getTime());

        ProductRandomKey = SaveCurrentDate+SaveCurrentTime;
        final StorageReference filepath = productImagesRef.child(imageUri.getLastPathSegment()+ProductRandomKey+".jpg");
        final UploadTask uploadTask = filepath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this,"ERROR : "+ message,Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this,"Product Image Uploaded Sucessfully",Toast.LENGTH_SHORT).show();
                Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                       if(!task.isSuccessful())
                       {
                           throw task.getException();
                       }
                       //Log.e("uploade_image", ""+filepath.getDownloadUrl().toString());
                       downloadImageUrl = filepath.getDownloadUrl().toString();
                       return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Log.e("uploade_image", ""+task.getResult().toString());
                            downloadImageUrl=task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Got the image url successfully!", Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void saveProductInfoToDatabase() {
        final HashMap<String, Object> productMap=new HashMap<>();
        productMap.put("pid", ProductRandomKey);
        productMap.put("date",SaveCurrentDate);
        productMap.put("time", SaveCurrentTime);
        productMap.put("description", Descrpition);
        Log.e("image_url", ""+downloadImageUrl);
        productMap.put("image_url", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("pname", Pname);

        databaseReference.child(ProductRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(AdminAddNewProductActivity.this, admin_category.class);
                    startActivity(intent);

                    loadingBar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");// this means /* i saari types accepted h like jpg png etc
        startActivityForResult(galleryIntent,GALLERYPICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERYPICK && resultCode==RESULT_OK && data!=null)
        {
            imageUri = data.getData();
            selectProductImage.setImageURI(imageUri);

        }
    }


}
