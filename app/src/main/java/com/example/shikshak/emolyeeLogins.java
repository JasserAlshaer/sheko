package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class emolyeeLogins extends AppCompatActivity {
    public CircleImageView employeeScreenImage;
    public TextView emailLabel,passwordLabel;
    public BootstrapEditText employeeEmailField,employeePasswordField;
    public FirebaseAuth mAuth;
    public String role;
    public static String emailofEmployee="";
     public void DefineAllScreenObject(){
         employeeScreenImage=findViewById(R.id.profile_image_employee);
         emailLabel=findViewById(R.id.emailTextView2);
         passwordLabel=findViewById(R.id.passwordTextView2);
         employeeEmailField=findViewById(R.id.emailTextField2);
         employeePasswordField=findViewById(R.id.passwordTextField2);
     }
     public void ImplementResponsiveDeign(int width,int height){
         //First Thing Implement Design for Image
         LinearLayout.LayoutParams mainImageDesign=new
                 LinearLayout.LayoutParams(width,((int)(height*0.235)));
         mainImageDesign.gravity= Gravity.CENTER;
         mainImageDesign.setMargins(0,(int)(height*0.003),0,0);
         employeeScreenImage.setLayoutParams(mainImageDesign);

         //Second Thing Implement Design for Text View
         LinearLayout.LayoutParams TextSize=new
                 LinearLayout.LayoutParams(width, (int) ((int)height*0.0900));
         TextSize.gravity= Gravity.CENTER;
         float size=(float) (width*0.0285);
         TextSize.setMargins((int) (width*0.0950),0,0,0);
         emailLabel.setLayoutParams(TextSize);
         emailLabel.setTextSize(size);
         passwordLabel.setLayoutParams(TextSize);
         passwordLabel.setTextSize(size);
         //Third Thing Implement Design for Edit Text
         LinearLayout.LayoutParams editTextDesign=
                 new LinearLayout.LayoutParams((int)(width*0.9), (int)(height*0.0525));
         editTextDesign.setMargins((int) (width*0.0250),(int)(height*0.00001),0,(int)(height*0.00001));
         employeeEmailField.setLayoutParams(editTextDesign);
         employeePasswordField.setLayoutParams(editTextDesign);
     }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emolyee_logins);
         mAuth = FirebaseAuth.getInstance();
        //Define All Object In Main Activity Screen
        DefineAllScreenObject();
        //Get Actual Size For Screen used by User
        Display displayManeger=getWindowManager().getDefaultDisplay();
        Point displayPoint=new Point();
        displayManeger.getSize(displayPoint);
        int width=displayPoint.x;
        int height=displayPoint.y;
        //Applay Design Pattern On Screen Content
        ImplementResponsiveDeign(width,height);

        //Now get Data from main Activity then Determine the Employee is role
        Intent determineReceiver=getIntent();
        role=determineReceiver.getStringExtra("role");

    }

    public void OnEmployeeLoginButtonClicked(View view) {
        if(role.equals("admin")){
            //get the email and password then Authenticate user using firebase

            String userEmail,userPassword;
            userEmail=employeeEmailField.getText().toString();
            userPassword=employeePasswordField.getText().toString();
            if(userEmail.equals("")||userPassword.equals("")){
                Toast.makeText(getApplicationContext(), "Please Enter Email And Password", Toast.LENGTH_SHORT).show();
            }else{
                //Continue Login Operation

                mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    if(mAuth.getCurrentUser().isEmailVerified()){
                                        // Sign in success, update UI with the signed-in user's information
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Admin");
                                        final Query gameQuery = ref.orderByChild("adminEmail").equalTo(userEmail);
                                        gameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                                    Intent goToAdminMainScreen=new
                                                            Intent(getApplicationContext(),admin_Control_Panel.class);
                                                    emailofEmployee=userEmail;
                                                    startActivity(goToAdminMainScreen);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Please Verify Email", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.i("Data", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        }else {
            //Login As delivery
            //get the email and password then Authenticate user using firebase

            String userEmail,userPassword;
            userEmail=employeeEmailField.getText().toString();
            userPassword=employeePasswordField.getText().toString();
            if(userEmail.equals("")||userPassword.equals("")){
                Toast.makeText(getApplicationContext(), "Please Enter Email And Password", Toast.LENGTH_SHORT).show();
            }else{
                //Continue Login Operation
                mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Delivery");
                                    final Query gameQuery = ref.orderByChild("deliveryManEmail").equalTo(userEmail);
                                    gameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                                Intent goToDeliveryMainScreen=new
                                                        Intent(getApplicationContext(),deliveryManScreen.class);
                                                emailofEmployee=userEmail;
                                                startActivity(goToDeliveryMainScreen);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.i("Data", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }});
            }
        }
    }
}