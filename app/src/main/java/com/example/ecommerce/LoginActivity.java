package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    EditText InputPhoneNumber,InputPassword;
    Button loginButton;
    CheckBox chkBoxRememberMe;
        ProgressDialog loadingBar;
        TextView adminpanelink,notadminpanelink,forgetpassword;
public String parentDbName="Users";

protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_activity);
    loginButton = (Button) findViewById(R.id.login_btn);
    InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
    adminpanelink = findViewById(R.id.admin_panel_link);
    notadminpanelink = findViewById(R.id.not_admin_panel_link);
    forgetpassword = findViewById(R.id.forget_password_link);
    InputPassword = (EditText) findViewById(R.id.login_password_input);
    loadingBar = new ProgressDialog(this);
    Paper.init(this);
    chkBoxRememberMe = (CheckBox)findViewById(R.id.chk_remember_me);


    loginButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LoginUser();
        }

        ;
    });
    adminpanelink.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loginButton.setText("Login admin");
            adminpanelink.setVisibility(View.INVISIBLE);
            notadminpanelink.setVisibility((View.VISIBLE));
            parentDbName="Admin";

        }
    });
    notadminpanelink.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loginButton.setText("Login user");
            adminpanelink.setVisibility(View.VISIBLE);
            notadminpanelink.setVisibility((View.INVISIBLE));
            parentDbName="Users";
        }
    });
    forgetpassword.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(LoginActivity.this,ResetPassword.class);
            intent.putExtra("check","login");
            startActivity(intent);
        }
    });
}
private void LoginUser() {
    String phone = InputPhoneNumber.getText().toString();
    String password = InputPassword.getText().toString();
    if (TextUtils.isEmpty(phone)) {
        Toast.makeText(this, "Please Enter your phone...", Toast.LENGTH_LONG).show();
    } else if (TextUtils.isEmpty(password)) {
        Toast.makeText(this, "Please Enter your password...", Toast.LENGTH_LONG).show();
    } else {
        loadingBar.setTitle("Join Account" );
        loadingBar.setMessage("Please wait,while we are checking the credentials" );
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Log.e("a","pass1");
        AllowAccessToAccount(phone, password);
    }
}
private void AllowAccessToAccount(final String phone,final String password) {
    if (chkBoxRememberMe.isChecked()) {
        Paper.book().write(Prevalant.UserPasswordKey, password);
        Paper.book().write(Prevalant.UserPhoneKey, phone);
    }
    Log.e("b","pass2");
    final DatabaseReference RootRef;
    RootRef = FirebaseDatabase.getInstance().getReference();
    RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.child(parentDbName).exists()) {
                Users userData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                if (userData.getPhone().equals(phone)) {
                    if (userData.getPassword().equals(password)) {

                        Log.e("c","password checked");
                        if (parentDbName.equals("Admin")) {
                            Log.e("b","in admin");
                            Toast.makeText(LoginActivity.this, "Welcome admin", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            Intent i = new Intent(LoginActivity.this, admin_category.class);
                            startActivity(i);
                        } else if (parentDbName.equals("Users")) {

                            Toast.makeText(LoginActivity.this, "Login SUccessfull", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            Prevalant.currentOnlineUser=userData;
                            Log.e("check login",""+Prevalant.currentOnlineUser.getName());
                            Log.e("check login",""+Prevalant.currentOnlineUser.getPhone());
                            startActivity(intent);

                        }
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(LoginActivity.this, "Account with this" + phone + "number does not exist", Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }
}

