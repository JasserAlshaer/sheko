package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class splash extends AppCompatActivity {
    public  CircleImageView logo;
    public TextView appName;
    private void ApplayResponsiveonImage(int width,int height){

         logo=(CircleImageView)findViewById(R.id.logoImage);
        LinearLayout.LayoutParams mainImageDesign=new
                LinearLayout.LayoutParams(width, (int) ((int)height*0.250));
        mainImageDesign.gravity= Gravity.CENTER;
        mainImageDesign.setMargins(0,((int)(height*0.1)),0,((int)(height*0.1)));
        logo.setLayoutParams(mainImageDesign);
    }
    private void ApplayResponsiveonText(int width,int height){
         appName=(TextView)findViewById(R.id.applicationName);
        LinearLayout.LayoutParams appliactionNameDesign=new
                LinearLayout.LayoutParams(width, (int) ((int)height*0.150));
        appliactionNameDesign.gravity= Gravity.CENTER;
        appName.setTextSize((float) (width*0.055));
    }
    private void SetAnimation(){
        logo.setY(-4200);
        appName.setX(-3500);
        logo.animate().translationYBy(4200).alpha(1000).setDuration(3500);
        appName.animate().translationXBy(3500).alpha(1000).rotation(360).setDuration(3500);
    }
    private void ApplayThread(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(4000);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Get The Actual Size of Screen
        Display displayManeger=getWindowManager().getDefaultDisplay();
        Point displayPoint=new Point();
        displayManeger.getSize(displayPoint);
        int width=displayPoint.x;
        int height=displayPoint.y;
        //Control The Design of Application Logo Image
        ApplayResponsiveonImage(width,height);
        //Control The Design of Application Name
        ApplayResponsiveonText(width,height);
        // Make Animation on Screen Content
        SetAnimation();
        //Activation Screen
        ApplayThread();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseDatabase.getInstance().getReference().child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    User s=snapshot1.getValue(User.class);
                    if(s.email.equals("joshaer17@gmail.com")) {
                        Toast.makeText(getApplicationContext(), snapshot1.getKey()+""+s.phone, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("Error",error.getDetails());
            }
        });



    }
}