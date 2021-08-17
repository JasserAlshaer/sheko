package com.example.shikshak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FinishOrder extends AppCompatActivity {
    public TextView totalPrice,userPayment,notesLabel;
    public EditText actuallUserPayment;
    public BootstrapEditText notesTextArea;
    public BootstrapButton finishOrderButton;

    //Define All Screen Objects
    public void DefineAllScreenContent(){
       totalPrice= findViewById(R.id.totalPriceOrderfinish);
       userPayment=findViewById(R.id.userpaymentLabel);
       notesLabel=findViewById(R.id.notesTextLabel);
       actuallUserPayment=findViewById(R.id.userPaymentEditText);
       notesTextArea=findViewById(R.id.notesTextArea);
       finishOrderButton=findViewById(R.id.finishOrderbutton);
    }
    //Mange The Design of Screen
    public void ApplayResponsiveDesign(int width,int height){
        LinearLayout.LayoutParams textViewDesign=new
                LinearLayout.LayoutParams((int)(width*0.90),(int)(height*0.0950));
        textViewDesign.setMargins((int)(width*0.050),(int)(height*0.0250),0,0);
        totalPrice.setLayoutParams(textViewDesign);
        userPayment.setLayoutParams(textViewDesign);
        notesLabel.setLayoutParams(textViewDesign);
        //Regular Edit Text
        LinearLayout.LayoutParams editViewDesignandButton=new
                LinearLayout.LayoutParams((int)(width*0.90),(int)(height*0.0950));
        editViewDesignandButton.setMargins((int)(width*0.050),(int)(height*0.0250),0,0);
        actuallUserPayment.setLayoutParams(editViewDesignandButton);
        finishOrderButton.setLayoutParams(editViewDesignandButton);
        //TextArea Design
        LinearLayout.LayoutParams textAreaDesign=new
                LinearLayout.LayoutParams((int)(width*0.90),(int)(height*0.16500));
        textAreaDesign.setMargins((int)(width*0.050),(int)(height*0.0350),0,0);
        notesTextArea.setLayoutParams(textAreaDesign);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_order);
        //Define All Screen Objects
        DefineAllScreenContent();
        //Get Actual Screen Size
        Display displayManeger=getWindowManager().getDefaultDisplay();
        Point displayPoint=new Point();
        displayManeger.getSize(displayPoint);
        int width=displayPoint.x;
        int height=displayPoint.y;
        //Mange The Design of Screen
        ApplayResponsiveDesign(width,height);
        Intent recive=getIntent();
        double total=Double.parseDouble(recive.getStringExtra("orderPrice"));
        String id=recive.getStringExtra("orderId");

        totalPrice.setText(total+"");
        finishOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double actualpay=Double.parseDouble(actuallUserPayment.getText().toString());
                String notes=notesTextArea.getText().toString();
                if(actualpay>=total){
                    changeOrderStatusAndFinish(id,notes);
                }else{
                    Toast.makeText(getApplicationContext(), "The Actual Pay Is Less Than Total Price", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private void changeOrderStatusAndFinish(String selectedOrderId,String orderNotes) {
        // this method used to change order status
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child("Orders");
        final Query applesQuery = ref;

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childnow: dataSnapshot.getChildren()) {
                        ShikOrder fetched=childnow.getValue(ShikOrder.class);
                        ref.child(selectedOrderId).child("orderStatus").setValue("Completed");
                        ref.child(selectedOrderId).child("orderComment").setValue(orderNotes);
                        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                        finish();

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Null", "onCancelled", databaseError.toException());
            }
        });
    }
}