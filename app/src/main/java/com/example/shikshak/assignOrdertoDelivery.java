package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class assignOrdertoDelivery extends AppCompatActivity {
    public ListView deliveryAvailableList;
    public ArrayAdapter deliveryEmployeeListAdapter;
    public ArrayList<String>employeeName,employeeImagePath,employeeListId;
    ProgressDialog createNewDialog;
    Intent getOrderId;
    String orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_orderto_delivery);
        employeeName=new ArrayList<String>();
        employeeImagePath=new ArrayList<String>();
        employeeListId=new ArrayList<String>();
        createNewDialog = new ProgressDialog(assignOrdertoDelivery.this);
        createNewDialog.setMessage("Please Wait ... ");
        createNewDialog.show();
        deliveryAvailableList=findViewById(R.id.deliveryEmployee);
        getOrderId=getIntent();
        orderId=getOrderId.getStringExtra("ID");
        registerForContextMenu(deliveryAvailableList);
    }
    @Override
    protected void onStart() {
        super.onStart();
        updateScreenContent();
    }

    private void updateScreenContent() {
        employeeImagePath.clear();
        employeeName.clear();
        employeeListId.clear();
        FirebaseApp.initializeApp(assignOrdertoDelivery.this);
        FirebaseDatabase.getInstance().getReference().child("Delivery").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    DeliveryMan man=child.getValue(DeliveryMan.class);
                    if(man.deliveryManHostRestaurantId.equals(admin_Control_Panel.resturnatsId)){
                        employeeName.add(man.deliveryManName);
                        employeeImagePath.add(man.deliveryManProfileImagePath);
                        employeeListId.add(child.getKey());
                    }
                }
                deliveryEmployeeListAdapter=new ArrayAdapter
                        (getApplicationContext(),R.layout.assignorderemployee,R.id.employeeNameFieldorder,employeeName){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view= super.getView(position, convertView, parent);
                        TextView employeeEmailTextView=view.findViewById(R.id.employeeEmailFieldorder);
                          employeeEmailTextView.setVisibility(View.INVISIBLE);
                        CircleImageView imageView=view.findViewById(R.id.employeeProfileImagegogo);
                        Glide.
                                with(getApplicationContext()).load(employeeImagePath.get(position)).into(imageView);
                        Button assign=view.findViewById(R.id.assignOrderToEmployee);
                        assign.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateDeliveryManToOrder(orderId,employeeListId.get(position));
                            }
                        });
                        return view;
                    }
                };
                deliveryAvailableList.setAdapter(deliveryEmployeeListAdapter);
                createNewDialog.dismiss();
                deliveryEmployeeListAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateDeliveryManToOrder(String selectedId,String employeeId) {
        // this method used to change order status
        /*DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Orders").child(selectedId);*/
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child("Orders");
        final Query applesQuery = ref.orderByChild("orderDeliveryMan").equalTo("");

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childnow: dataSnapshot.getChildren()) {
                        ShikOrder fetched=childnow.getValue(ShikOrder.class);
                        ref.child(selectedId).child("orderDeliveryMan").setValue(employeeId);
                        ref.child(selectedId).child("orderStatus").setValue("processing");
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