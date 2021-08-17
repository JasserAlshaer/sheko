package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Objects;

public class latestOrder extends AppCompatActivity {
    public ListView pendingOrderList;
    public ArrayAdapter pendingOrderListAdapter;
    public ArrayList<ShikOrder> latestOrderItems;
    public ArrayList<String> myLatestOrderIds,orderSenderName;
    public int myCurrentPosition=0;
    ProgressDialog createNewAdminDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_order17);
        pendingOrderList=findViewById(R.id.pendingOrderListViewinApp);
        orderSenderName=new ArrayList<String>();

        latestOrderItems =new ArrayList<ShikOrder>();
        myLatestOrderIds=new ArrayList<String>();
        createNewAdminDialog = new ProgressDialog(latestOrder.this);
        createNewAdminDialog.setMessage("Please Wait ... ");
        createNewAdminDialog.show();
        //Invoke The On Click Listener
        pendingOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        pendingOrderList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                myCurrentPosition=position;
                return false;
            }
        });
        registerForContextMenu(pendingOrderList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateScreenContent();
    }

    public void updateScreenContent() {
        orderSenderName.clear();
        latestOrderItems.clear();
        myLatestOrderIds.clear();
        FirebaseApp.initializeApp(latestOrder.this);
        FirebaseDatabase.getInstance().getReference().child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot Orderschild : snapshot.getChildren()) {
                    ShikOrder fetchedItem = Orderschild.getValue(ShikOrder.class);
                    if (fetchedItem.resturantId.equals(admin_Control_Panel.resturnatsId)
                       && fetchedItem.orderStatus.equals("unComplete")) {
                        orderSenderName.add(fetchedItem.orderOwnerId);
                        latestOrderItems.add(fetchedItem);
                        myLatestOrderIds.add(Orderschild.getKey());
                    }
                }
                // fill Array
                pendingOrderListAdapter=new ArrayAdapter
                        (getApplicationContext(),R.layout.orderlistviewdesign,R.id.customerName,orderSenderName){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view= super.getView(position, convertView, parent);
                        TextView totalTextView=view.findViewById(R.id.totalPriceTextLabel);
                        totalTextView.setText(latestOrderItems.get(position).totalPrice);
                        TextView DateTextView=view.findViewById(R.id.dateTextLabel);
                        DateTextView.setText(latestOrderItems.get(position).ordertimeandDate);
                        Button accept=view.findViewById(R.id.acceptOrderOffer);

                        accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeOrderStatus(myLatestOrderIds.get(position));
                                Intent setOrderForEmployee=new Intent(latestOrder.this,assignOrdertoDelivery.class);
                                setOrderForEmployee.putExtra("ID",myLatestOrderIds.get(position));
                                startActivity(setOrderForEmployee);
                            }
                        });
                        return view;
                    }
                };
                pendingOrderList.setAdapter(pendingOrderListAdapter);
                createNewAdminDialog.dismiss();
                pendingOrderListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeOrderStatus(String selectedOrderId) {
        // this method used to change order status
        // UPDATE DATABASE START
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child("Orders");
        final Query applesQuery = ref.orderByChild("ordertimeandDate").equalTo(latestOrderItems.get(myCurrentPosition).ordertimeandDate);
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot childnow: snapshot.getChildren()) {
                        ShikOrder fetched=childnow.getValue(ShikOrder.class);
                        ref.child(childnow.getKey()).child("orderStatus").setValue("pending");
                        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                        updateScreenContent();
                        //finish();

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId()==R.id.pendingOrderListViewinApp){
            AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("What Do You Want To Do ? :");
            String [] menuItem=getResources().getStringArray(R.array.pendingorderoption);
            for(int i=0;i<menuItem.length;i++){
                menu.add(Menu.NONE,i,i,menuItem[i]);
            }
        }
    }
    private void deleteItemsFromDatabase(String selectedId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Orders").child(selectedId);


        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Null", "onCancelled", databaseError.toException());
            }
        });
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex=item.getItemId();
        String [] menuItem=getResources().getStringArray(R.array.options);
        String menuItemName=menuItem[menuItemIndex];
        switch (menuItemIndex){
            case 0:
                deleteItemsFromDatabase(myLatestOrderIds.get(myCurrentPosition));
                break;
        }
        return super.onContextItemSelected(item);
    }
}