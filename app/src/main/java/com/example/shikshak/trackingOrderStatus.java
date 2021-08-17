package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class trackingOrderStatus extends AppCompatActivity {
    public ImageView mainStatusImage;
    public TextView mainStatusText;
    public Button mainButton;
    public CountDownTimer trackingOrder;
    public void DefineAllScreenObject(){
        mainStatusImage=findViewById(R.id.mainImageTracking);
        mainStatusText=findViewById(R.id.mainTextMessageTracking);
        mainButton=findViewById(R.id.mainButtonTracking);
    }
    public void ImplementResponsiveDeign(int width,int height){
        LinearLayout.LayoutParams mainImageDesign=new
                LinearLayout.LayoutParams(width,((int)(height*0.340)));
        mainImageDesign.gravity= Gravity.CENTER;
        mainImageDesign.setMargins(0,(int)(height*0.020),0,(int)(height*0.060));
        mainStatusImage.setLayoutParams(mainImageDesign);
        //Text View
        LinearLayout.LayoutParams TextSize=new
                LinearLayout.LayoutParams(width, (int) ((int)height*0.19500));
        TextSize.gravity= Gravity.CENTER;
        TextSize.setMargins(0,0,0,0);
        mainStatusText.setLayoutParams(TextSize);
        //Button
        LinearLayout.LayoutParams buttonDesign=new
                LinearLayout.LayoutParams(width, (int) ((int)height*0.12500));
        buttonDesign.gravity= Gravity.CENTER;
        buttonDesign.setMargins(0,(int)(height*0.125),0,0);
        mainButton.setLayoutParams(buttonDesign);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order_status);
        DefineAllScreenObject();
        //Get Actual Size For Screen used by User
        Display displayManeger=getWindowManager().getDefaultDisplay();
        Point displayPoint=new Point();
        displayManeger.getSize(displayPoint);
        int width=displayPoint.x;
        int height=displayPoint.y;
        //Applay Design Pattern On Screen Content
        mainButton.setVisibility(View.INVISIBLE);
        ImplementResponsiveDeign(width,height);
       /*long time =1000*60*20;
        long counter=1000*60*3;*/
        long time =1000*60*30;
        long counter=1000*20;
        trackingOrder=new CountDownTimer(time,counter){

            @Override
            public void onTick(long millisUntilFinished) {
                // UPDATE DATABASE START

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference ref = database.child("Orders");
                final Query gameQuery = ref.orderByChild("orderOwnerId").equalTo(MainActivity.usermail);

                gameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot childnow: snapshot.getChildren()) {
                                ShikOrder fetched=childnow.getValue(ShikOrder.class);
                                if(fetched.ordertimeandDate.equals(determineUserLocation.date)){
                                    updateScreenContent(fetched.orderStatus);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                //UPDATE DATABASE END
            }

            @Override
            public void onFinish() {
                mainButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void updateScreenContent(String orderStatus) {
        switch (orderStatus){
            case "unComplete":
                Log.i("Status","Still Un Complete");
                mainStatusImage.setImageResource(R.drawable.wait);
                mainStatusText.setText("Your Order Send To Restaurants");
                break;
            case "pending":
                Log.i("Status","Still  Pending");
                mainStatusImage.setImageResource(R.drawable.wait);
                mainStatusText.setText("Your Order Send To Restaurants and Accepted");
                break;
            case "processing":
                Log.i("Status","Still  Processing");
                mainStatusImage.setImageResource(R.drawable.preparation);
                mainStatusText.setText("Your Order Send To Restaurants and Start Preparations");
                break;
            case "Completed":
                Log.i("Status","Still  Completed");
                mainStatusImage.setImageResource(R.drawable.completed);
                mainStatusText.setText("Your Order Are Completed Successfully");
                mainButton.setVisibility(View.VISIBLE);
                trackingOrder.cancel();
                break;
        }
    }

    public void backToHomeScreen(View view) {
        Intent goToHome=new Intent(trackingOrderStatus.this,mainUserAppScreen.class);
        startActivity(goToHome);
    }
}