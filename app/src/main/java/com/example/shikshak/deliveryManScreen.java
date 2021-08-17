package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class deliveryManScreen extends AppCompatActivity {
    public BootstrapButton assignedOrder,completedOrders;
    public CircleImageView mainImage;
    public static String deliveryManId;
    ProgressDialog createNewAdminDialog;
    public void DefineAllObjects(){
        mainImage=findViewById(R.id.mainScreenImage);
        assignedOrder=findViewById(R.id.assignedOrderButton);
        completedOrders=findViewById(R.id.completedOrdersButton);
    }
    public void ResponsiveDesignPattern(int width,int height){
        LinearLayout.LayoutParams imageViewDesign=new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,(int)(height*0.4750));
        imageViewDesign.setMargins(0,(int)(height*0.035),0,(int)(height*0.035));
        imageViewDesign.gravity= Gravity.CENTER;
        mainImage.setLayoutParams(imageViewDesign);
        //Buttons Design
        LinearLayout.LayoutParams buttonDesign=new
                LinearLayout.LayoutParams((int)(width*0.85),(int)(height*0.1150));
        buttonDesign.setMargins((int)(width*0.05),(int)(height*0.135),0,0);
        assignedOrder.setLayoutParams(buttonDesign);
        completedOrders.setLayoutParams(buttonDesign);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_man_screen);
        //Define Mirror Object For All Screen Content
        DefineAllObjects();
        //Get Actual Size For Screen used by User
        Display displayManeger=getWindowManager().getDefaultDisplay();
        Point displayPoint=new Point();
        displayManeger.getSize(displayPoint);
        int width=displayPoint.x;
        int height=displayPoint.y;
        //Define Responsive Design Pattern
        ResponsiveDesignPattern(width,height);
        completedOrders.setVisibility(View.INVISIBLE);
        assignedOrder.setVisibility(View.INVISIBLE);
        createNewAdminDialog = new ProgressDialog(deliveryManScreen.this);
        createNewAdminDialog.setMessage("Please Wait ... ");
        createNewAdminDialog.show();
        getManId();

    }

    public void getAssignedOrders(View view) {
        Intent moveToInteractiveWithOrders=new Intent(deliveryManScreen.this,assigndOrderList.class);
        startActivity(moveToInteractiveWithOrders);
    }
    public  void getManId(){
        // this method used to change order status
        FirebaseDatabase.getInstance().getReference().child("Delivery").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(getApplicationContext(), emolyeeLogins.emailofEmployee, Toast.LENGTH_LONG).show();
                for (DataSnapshot child: snapshot.getChildren()) {
                    DeliveryMan man=child.getValue(DeliveryMan.class);
                    if(man.deliveryManEmail.equals(emolyeeLogins.emailofEmployee)){
                        deliveryManId=child.getKey();
                        Toast.makeText(getApplicationContext(), deliveryManId, Toast.LENGTH_LONG).show();
                        assignedOrder.setVisibility(View.VISIBLE);
                        createNewAdminDialog.dismiss();
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}