package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPassword extends AppCompatActivity {
    TextView pagetitle, titleques;
    String check = "";
    EditText pnumber, q1, q2;
    Button verfiybtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        check = getIntent().getStringExtra("check");
        pagetitle = findViewById(R.id.page_title);
        titleques = findViewById(R.id.title_questions);
        pnumber = findViewById(R.id.find_phone_number);
        q1 = findViewById(R.id.question_1);
        q2 = findViewById(R.id.question_2);
        verfiybtn = findViewById(R.id.verify_btn);


    }

    @Override
    protected void onStart() {
        super.onStart();
        pnumber.setVisibility(View.GONE);
        if (check.equals("settings")) {
            displayPreviousAnswers();
            pagetitle.setText("Set Questions");
            titleques.setText("Please set Answers for the following questions ");
            verfiybtn.setText("Set");

            verfiybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAnswers();
                }
            });
        } else if (check.equals("login")) {
            pnumber.setVisibility(View.VISIBLE);
            verfiybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUser();
                }
            });
        }
    }

    private void verifyUser() {
        final String phone=pnumber.getText().toString();
        final String answer1=q1.getText().toString().toLowerCase();
        final String answer2=q2.getText().toString().toLowerCase();
        Log.e("A",phone);
        Log.e("A",answer1);
        Log.e("A",answer2);

        if(!phone.equals("") && !answer1.equals("") && !answer2.equals("")) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(phone);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        String mPhone = dataSnapshot.child("phone").getValue().toString();
                        if(dataSnapshot.hasChild("Security Questions")) {
                            String ans1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();
                            if(!ans1.equals(answer1))
                            {
                                Toast.makeText(ResetPassword.this,"Your first answer is wrong ",Toast.LENGTH_LONG).show();
                            }
                            else if(!ans2.equals(answer2))
                            {
                                Toast.makeText(ResetPassword.this,"Your second answer is wrong ",Toast.LENGTH_LONG).show();

                            }
                            else
                            {
                                AlertDialog.Builder builder=new AlertDialog.Builder(ResetPassword.this);
                                builder.setTitle("Write New Password ");
                                final EditText newPassword=new EditText(ResetPassword.this);
                                newPassword.setHint("Write Password here...");
                                builder.setView(newPassword);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(!newPassword.getText().toString().equals(""))
                                        {
                                            ref.child("password").setValue(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful())
                                                            {
                                                                Toast.makeText(ResetPassword.this,"Password changed successfully",Toast.LENGTH_LONG).show();
                                                                Intent intent=new Intent(ResetPassword.this,LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        }
                        else
                        {
                            Toast.makeText(ResetPassword.this,"You have not set the security questions",Toast.LENGTH_LONG).show();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(ResetPassword.this,"Please complete the form",Toast.LENGTH_LONG).show();
        }
    }

    private void setAnswers() {
        String answer1 = q1.getText().toString().toLowerCase();
        String answer2 = q2.getText().toString().toLowerCase();

        if (q1.equals("") && q2.equals("")) {
            Toast.makeText(ResetPassword.this, "Please answer both questions ", Toast.LENGTH_LONG).show();
            q1.setError("Please answer both questions");

        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalant.currentOnlineUser.getPhone());
            HashMap<String, Object> userdatamap = new HashMap<>();
            userdatamap.put("answer1", answer1);
            userdatamap.put("answer2", answer2);
            ref.child("Security Questions").updateChildren(userdatamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ResetPassword.this,"You have successfully set the security questions",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(ResetPassword.this,HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

        private void displayPreviousAnswers () {
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalant.currentOnlineUser.getPhone());
            ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        String ans1=dataSnapshot.child("answer1").getValue().toString();
                        String ans2=dataSnapshot.child("answer2").getValue().toString();
                        q1.setText(ans1);
                        q2.setText(ans2);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


