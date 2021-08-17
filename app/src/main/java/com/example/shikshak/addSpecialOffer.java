package com.example.shikshak;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class addSpecialOffer extends AppCompatActivity {
    public ImageView applayFreeDelivery,applayTenPercentDiscount;
    public void DefineAllScreenObjects(){
        applayFreeDelivery=findViewById(R.id.freedeliveryImage);
        applayTenPercentDiscount=findViewById(R.id.discountImage);
    }
    public void ApplyResponsiveDesign(int width,int height){
        LinearLayout.LayoutParams ImageViewDesign=new
                LinearLayout.LayoutParams((int)(width*0.90),(int)(height*0.435));
        ImageViewDesign.setMargins((int)(width*0.05),(int)(height*0.025),0,0);
        applayTenPercentDiscount.setLayoutParams(ImageViewDesign);
        applayFreeDelivery.setLayoutParams(ImageViewDesign);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_special_offer);
        //Get Actual Size For Screen used by User
        Display displayManeger=getWindowManager().getDefaultDisplay();
        Point displayPoint=new Point();
        displayManeger.getSize(displayPoint);
        int width=displayPoint.x;
        int height=displayPoint.y;
        //Define All Objects
        DefineAllScreenObjects();
        //Applay Design Patern
        ApplyResponsiveDesign( width, height);
        //Applay Offers
        applayFreeDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UPDATE DATABASE START

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference ref = database.child("Restaurant");
                final Query gameQuery = ref.orderByChild("restaurantOwnerId").equalTo(admin_Control_Panel.ownerId);

                gameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot child: snapshot.getChildren()) {
                                ref.child(child.getKey()).child("avilableOffer").setValue("freeDelivery");
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                //UPDATE DATABASE END
            }
        });
        applayTenPercentDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UPDATE DATABASE START
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference ref = database.child("Restaurant");
                final Query gameQuery = ref.orderByChild("restaurantOwnerId").equalTo(admin_Control_Panel.ownerId);

                gameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot child: snapshot.getChildren()) {
                                ref.child(child.getKey()).child("avilableOffer").setValue("discount");
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                //UPDATE DATABASE END
            }
        });
    }

}