package com.example.ecommerce;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class setting_activity extends AppCompatActivity {
    private ImageView profileimageView;
    private EditText edfullname,eduserphone,edaddress;
    private TextView profilechangetxtbtn,closetextbtn,savetextbtn;

    private StorageReference storageProfilePicReference;
    private StorageTask uploadtask;

    private Uri imageuri;
    private String imageUrl="";
    boolean check;
    Button seqcheckbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_activity);

        storageProfilePicReference = FirebaseStorage.getInstance().getReference().child("profile pictures");

        profileimageView = findViewById(R.id.setting_profile_image);
        edfullname = findViewById(R.id.settings_full_name);
        eduserphone = findViewById(R.id.setting_phone_number);
        edaddress = findViewById(R.id.seeting_address);
        profilechangetxtbtn = findViewById(R.id.profile_image_change_btn);
        closetextbtn = findViewById(R.id.close_settings_btn);
        savetextbtn = findViewById(R.id.update_account_seeting_btn);
        seqcheckbtn = findViewById(R.id.security_questions_btn);
        userInfoDisplay(profileimageView,edfullname,eduserphone,edaddress);

        closetextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                }
            });
        savetextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check){
                    Log.e("in upload image", "true");
                    userInfosaved();
                }
                else{
                    Log.e(" not in upload image", "false");
                    updateOnlyUserInfo();
                }
            }
        });
        profilechangetxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = true;
                CropImage.activity(imageuri).setAspectRatio(1,1).start(setting_activity.this);
            }
        });
        seqcheckbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(setting_activity.this,ResetPassword.class);
                intent.putExtra("check","settings");
                startActivity(intent);
            }
        });
        }

    private void updateOnlyUserInfo() {
        Log.e("A","update only user info");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("name",edfullname.getText().toString());
        userMap.put("address",edaddress.getText().toString());
        userMap.put("Phonenumberfororder",eduserphone.getText().toString());
        ref.child(Prevalant.currentOnlineUser.getPhone()).updateChildren(userMap);
        Intent i = new Intent(setting_activity.this,HomeActivity.class);
        startActivity(i);
        Toast.makeText(setting_activity.this,"Profile Info Update Successfully",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void userInfosaved() {
        if(TextUtils.isEmpty(edfullname.getText().toString())){
            edfullname.setError("full name field is empty");
        }
        else if(TextUtils.isEmpty(eduserphone.getText().toString())){
            eduserphone.setError("Phone Number feild is empty`");
        }
        else if(TextUtils.isEmpty(edaddress.getText().toString())){
            edaddress.setError("Address feild is empty`");
        }
        else if(check){
            UploadImage();
        }

    }

    private void UploadImage() {
        Log.e("A","Upload Image");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait while we are updating your account info");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageuri!=null){
            Log.e("user data", Prevalant.currentOnlineUser.getName());
            final StorageReference fileRef=storageProfilePicReference.child(Prevalant.currentOnlineUser.getPhone()+".jpg");
            uploadtask=fileRef.putFile(imageuri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        imageUrl=task.getResult().toString();
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String,Object> userMap=new HashMap<>();
                        userMap.put("image", imageUrl);
                        userMap.put("name",edfullname.getText().toString());
                        userMap.put("address",edaddress.getText().toString());
                        userMap.put("Phonenumberfororder",eduserphone.getText().toString());
                        ref.child(Prevalant.currentOnlineUser.getPhone()).updateChildren(userMap);
                        progressDialog.dismiss();
                        startActivity(new Intent(setting_activity.this,HomeActivity.class));
                        Toast.makeText(setting_activity.this,"Profile Info Update Sucessfuly",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(setting_activity.this,"ERROR",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(setting_activity.this, "Error image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final ImageView profileimageView, final EditText edfullname, final EditText eduserphone, final EditText edaddress) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalant.currentOnlineUser.getPhone());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileimageView);
                        edfullname.setText(name);
                        eduserphone.setText(phone);
                        edaddress.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null) {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
                imageuri = result.getUri();
                profileimageView.setImageURI(imageuri);
                //Uri resultUri = result.getUri();
        }
        else{
            Toast.makeText(this,"Error Try Again",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(setting_activity.this,setting_activity.class);
            startActivity(i);
            finish();
        }
    }
}
