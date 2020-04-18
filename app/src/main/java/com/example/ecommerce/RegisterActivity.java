package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static com.google.firebase.database.FirebaseDatabase.getInstance;
public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    Button CreateAccountButton;
    EditText InputName, InputPhoneNumber, InputPaasword;
    SignInButton googlesignbtn;
    ProgressDialog loadingBar;
    private static final int RC_SIGN_IN=1;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API).build();
        CreateAccountButton = (Button) findViewById(R.id.register_btn);
        googlesignbtn = findViewById(R.id.sign_in_button);
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        InputPaasword = (EditText) findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);
// Third part starts here
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
        googlesignbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(i,RC_SIGN_IN);

            }
        });
    }

    private void CreateAccount() {
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPaasword.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please Enter your name...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please Enter your phone...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter your password...", Toast.LENGTH_LONG).show();
        } else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait,while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            ValidatePhoneNumber(name, phone, password);
        }
    }

    public void ValidatePhoneNumber(final String name, final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("Users").child(phone).exists()) {
                    HashMap<String, Object> userdatamap = new HashMap<>();
                    userdatamap.put("phone", phone);
                    userdatamap.put("password", password);
                    userdatamap.put("name", name);
                    RootRef.child("Users").child(phone).updateChildren(userdatamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Congratulations your account has been registered", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(i);
                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Network error", Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "this" + phone + "already exists ..either sign in or try with some other number", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            String temp = result.getSignInAccount().getGivenName();
            Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
            if(result.isSuccess()){
              //  Intent i = new Intent(RegisterActivity.this,RegisterActivity.class);
           // InputPhoneNumber.setText(result.getSignInAccount());
                Intent i = new Intent(RegisterActivity.this,RegisterActivity.class);
            //    Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
                InputName.setText(temp);
                startActivity(i);
            }
            else{
                Toast.makeText(this, "Login FAILED", Toast.LENGTH_SHORT).show();

            }
        }
    }
}