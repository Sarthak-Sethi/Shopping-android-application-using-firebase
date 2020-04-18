package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.Model.Users;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity  {
    Button btnlogin,btnjoin;
    ProgressDialog loadingBar;

    public String parentDbName="Users";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        btnlogin=findViewById(R.id.main_login_btn);
        loadingBar=new ProgressDialog(this);

        btnjoin=findViewById(R.id.main_join_now_btn);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
        btnjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

            String UserPhoneKey = Paper.book().read(Prevalant.UserPhoneKey);
            String UserPasswordKey = Paper.book().read(Prevalant.UserPasswordKey);
            if(UserPhoneKey!="" && UserPasswordKey!="")
            {
                if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey))
                {
                    AllowAccessToAccount(UserPhoneKey,UserPasswordKey);
                    loadingBar.setTitle("ALready login");
                    loadingBar.setMessage("Please wait................");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                }
            }

    }


    private void AllowAccessToAccount(final String UserPhoneKey,final String UserPasswordKey) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(UserPhoneKey).exists()) {
                    Users userData = dataSnapshot.child("Users").child(UserPhoneKey).getValue(Users.class);
                    if (userData.getPhone().equals(UserPhoneKey)) {
                        if (userData.getPassword().equals(UserPasswordKey)) {
                            Toast.makeText(MainActivity.this, "Login SUccessfull", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalant.currentOnlineUser = userData;
                            startActivity(intent);
                        } else {
                            loadingBar.dismiss();

                            Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();

                        }
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Account with this" + UserPhoneKey + "number does not exist", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}