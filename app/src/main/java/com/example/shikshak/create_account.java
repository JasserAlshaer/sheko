package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class create_account extends AppCompatActivity {
    public RadioButton userType,AdminType;
    public BootstrapEditText userName,userEmail,userPhone,userPassword;
    public FirebaseAuth mAuth;
    public FirebaseDatabase database;
    public DatabaseReference databaseReference;
    public static String generatedId="";
    protected void DefineAllScreenObject() {
        userType=findViewById(R.id.userRadioButtonChooies);
        AdminType=findViewById(R.id.AdminRadioButtonChooies);
        userName=findViewById(R.id.newUserNameField);
        userPhone=findViewById(R.id.newUserPhoneField);
        userEmail=findViewById(R.id.newUserEmailField);
        userPassword=findViewById(R.id.newUserPasswordField);
    }
    protected void RadioButtonResponsiveDesign(int width,int height){
        LinearLayout.LayoutParams radioDesign=new
                LinearLayout.LayoutParams((int) (width*0.310), ViewGroup.LayoutParams.WRAP_CONTENT);
        radioDesign.setMargins((int) (width*0.0950),(int)(height*0.022),(int) (width*0.00),(int)(height*0.022));
        AdminType.setLayoutParams(radioDesign);
        userType.setLayoutParams(radioDesign);
        AdminType.setTextSize((float) (width*0.029));
        userType.setTextSize((float) (width*0.029));
    }
    protected void EditTextResponsiveDesign(int width,int height){

        LinearLayout.LayoutParams editTextDesign=
                new LinearLayout.LayoutParams((int)(width*0.9), (int)(height*0.0625));
        editTextDesign.setMargins(0,(int)(height*0.009),0,0);
        userName.setLayoutParams(editTextDesign);
        userEmail.setLayoutParams(editTextDesign);
        userPhone .setLayoutParams(editTextDesign);
        userPassword.setLayoutParams(editTextDesign);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        //Define Instance of firebase
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();
        //Define All Object In Main Activity Screen
        DefineAllScreenObject();
        //Get Actual Size For Screen used by User
        Display displayManeger=getWindowManager().getDefaultDisplay();
        Point displayPoint=new Point();
        displayManeger.getSize(displayPoint);
        int width=displayPoint.x;
        int height=displayPoint.y;
        //Applay Design on Radio Button
        RadioButtonResponsiveDesign(width,height);
        //Applay Design on Edit Text
        EditTextResponsiveDesign(width,height);

        //Push Data To Fire Base
    }
    public void OnContinueSignUpButtonClicked(View view) {
        String name,email,phone,password;
        mAuth = FirebaseAuth.getInstance();
        name=userName.getText().toString();
        email=userEmail.getText().toString();
        phone=userPhone.getText().toString();
        password=userPassword.getText().toString();

        if(name.equals("")||email.equals("")||phone.equals("")||password.equals("")){
            Toast.makeText(getApplicationContext(), "Please Fill The Required Data", Toast.LENGTH_SHORT).show();
        }else{
            if(userType.isChecked()){
                // now if the user decided to sign up as user this junk of code will run
                //This code have one functionality that this code will send the data to next screen
                //To complete registration
                ProgressDialog createNewUserDialog;
                createNewUserDialog = new ProgressDialog(create_account.this);
                createNewUserDialog.setMessage("Please Wait ... ");
                createNewUserDialog.show();
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                User newUserOnShikApp=new User(name,email,phone);
                                                databaseReference.child("User").push().setValue(newUserOnShikApp);
                                                createNewUserDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Please Verify Your email", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Data", "createUserWithEmail:failure", task.getException());
                                    createNewUserDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }else if (AdminType.isChecked()){
                // now if the user decided to sign up as Resturant admin  this junk of code will run
                //This code have one functionality that this code will send the data to next screen
                //To complete registration
                ProgressDialog createNewAdminDialog;
                createNewAdminDialog = new ProgressDialog(create_account.this);
                createNewAdminDialog.setMessage("Please Wait ... ");
                createNewAdminDialog.show();
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Admin newshikShakAdmin= new Admin(name,email,phone);
                                                databaseReference.child("Admin").push().setValue(newshikShakAdmin);
                                                createNewAdminDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Please Verify Your Email", Toast.LENGTH_SHORT).show();
                                                Intent moveToAddNewResturnats=new Intent(getApplicationContext(),addNewResturants.class);
                                                moveToAddNewResturnats.putExtra("AdminEmail",email);
                                                startActivity(moveToAddNewResturnats);
                                            }
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    createNewAdminDialog.dismiss();
                                    Log.w("Data", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed."+task.getException(),
                                            Toast.LENGTH_LONG).show();

                                }
                            }
                        });
            }
            else{
                // other wise this massage will displayed if the user dose not select any type
                // of account options
                Toast.makeText(this, "Please select the type of you Account ", Toast.LENGTH_SHORT).show();
            }
        }
    }

}