package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class assigndOrderList extends AppCompatActivity {
    public ListView processingOrderList;
    public ArrayAdapter processingOrderListAdapter;
    public ArrayList<ShikOrder> latestOrderItems2;
    public ArrayList<String> myLatestOrderIds2,orderSenderName2;
    public int myCurrentPosition=0;
    ProgressDialog createNewAdminDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignd_order_list);
        processingOrderList=findViewById(R.id.assigndOrderList);
        orderSenderName2=new ArrayList<String>();

        latestOrderItems2 =new ArrayList<ShikOrder>();
        myLatestOrderIds2=new ArrayList<String>();
        createNewAdminDialog = new ProgressDialog(assigndOrderList.this);
        createNewAdminDialog.setMessage("Please Wait ... ");
        createNewAdminDialog.show();
        registerForContextMenu(processingOrderList);
        //Invoke The On Click Listener
        processingOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        processingOrderList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateScreenContent();
    }
    private void updateScreenContent() {
        orderSenderName2.clear();
        latestOrderItems2.clear();
        myLatestOrderIds2.clear();
        FirebaseApp.initializeApp(assigndOrderList.this);
        FirebaseDatabase.getInstance().getReference().child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot Orderschild : snapshot.getChildren()) {
                    ShikOrder fetchedItem = Orderschild.getValue(ShikOrder.class);
                    if (fetchedItem.orderStatus.equals("processing")) {
                        orderSenderName2.add(fetchedItem.orderOwnerId);
                        latestOrderItems2.add(fetchedItem);
                        myLatestOrderIds2.add(Orderschild.getKey());
                    }
                }
                // fill Array
                processingOrderListAdapter=new ArrayAdapter
                        (getApplicationContext(),R.layout.orderitemlistdesign,R.id.orderforLabel,orderSenderName2){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view= super.getView(position, convertView, parent);
                        TextView orderFor=view.findViewById(R.id.orderforLabel);
                        TextView totalPrice=view.findViewById(R.id.totalPriceLabel);
                        TextView Distance=view.findViewById(R.id.OrderDate);
                        Button directions=view.findViewById(R.id.getDirectionsButton);
                        Button finish=view.findViewById(R.id.finishOrderButtonByDelivery);

                        orderFor.setText(latestOrderItems2.get(position).orderOwnerId);
                        totalPrice.setText(latestOrderItems2.get(position).totalPrice);
                        Distance.setText(latestOrderItems2.get(position).ordertimeandDate);

                        directions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Get Direction For Selected Order
                                String uri = String.format(Locale.getDefault(), "http://maps.google.com/maps?q=loc:%f,%f"
                                        , latestOrderItems2.get(position).latitude
                                        ,latestOrderItems2.get(position).longtiude);
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);
                            }
                        });

                        finish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //finish Selected Order
                                //changeOrderStatus(myLatestOrderIds2.get(position));

                                Intent moveTofinshOrdder=new Intent(assigndOrderList.this,FinishOrder.class);
                                moveTofinshOrdder.putExtra("orderId",myLatestOrderIds2.get(position));
                                moveTofinshOrdder.putExtra("orderPrice",latestOrderItems2.get(position).totalPrice);
                                startActivity(moveTofinshOrdder);
                            }
                        });
                        return view;
                    }
                };
                processingOrderList.setAdapter(processingOrderListAdapter);
                createNewAdminDialog.dismiss();
                processingOrderListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}