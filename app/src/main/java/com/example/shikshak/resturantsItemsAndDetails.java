package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;

public class resturantsItemsAndDetails extends AppCompatActivity {
    public ListView availableItemsList;
    public ArrayAdapter availableItemsListAdapter;
    public static  ArrayList<Items> availableItems;
    public ArrayList<String> availableItemsNames,myItemsIds;
    public String hostId="";
    private ProgressDialog createNewDialog;
    public static HashMap<String,Integer> cartOfItems;
    public static HashMap<String,Double> pricing;
    public static String targetedId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resturants_items_and_details);
        availableItemsList=findViewById(R.id.resturantsItemsList);
        availableItems=new ArrayList<Items>();
        availableItemsNames=new ArrayList<String>();
        myItemsIds=new ArrayList<String>();
        cartOfItems=new HashMap<String,Integer> ();
        pricing=new HashMap<String,Double> ();
        Intent receiver=getIntent();
        hostId=receiver.getStringExtra("selectedId");
        targetedId=hostId;
        updateScreenData();

    }
    private void updateScreenData() {
        availableItemsNames.clear();
        availableItems.clear();
        createNewDialog = new ProgressDialog(resturantsItemsAndDetails.this);
        createNewDialog.setMessage("Please Wait ... ");
        createNewDialog.show();
        FirebaseApp.initializeApp(resturantsItemsAndDetails.this);
        FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    Items fetchedItem=child.getValue(Items.class);
                    if(fetchedItem.itemHostResturantId.equals(hostId)){
                        availableItemsNames.add(fetchedItem.itemName);
                        availableItems.add(fetchedItem);
                        myItemsIds.add(child.getKey());
                    }
                }
                availableItemsListAdapter=new ArrayAdapter
                        (getApplicationContext(),R.layout.seclectitemsfromlist,R.id.ItemsNameField,availableItemsNames){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view= super.getView(position, convertView, parent);
                        TextView employeeEmailTextView=view.findViewById(R.id.PriceField);
                        employeeEmailTextView.setText(availableItems.get(position).itemPrice);
                        TextView DescriptionView=view.findViewById(R.id.descriptionField);
                        DescriptionView.setText(availableItems.get(position).itemdescreption+"");
                        CircleImageView imageView=view.findViewById(R.id.itemImage);
                        Glide.
                                with(getApplicationContext()).load(availableItems
                                .get(position).itemImagePath).into(imageView);
                        Button addToCart=view.findViewById(R.id.addingitemsButton);
                        addToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addItemIntoHashMapCart
                             (availableItemsNames.get(position),Double.parseDouble(availableItems.get(position).itemPrice));
                                //Toast.makeText(resturantsItemsAndDetails.this,  cartOfItems.size()+"", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return view;
                    }
                };
                availableItemsList.setAdapter(availableItemsListAdapter);
                createNewDialog.dismiss();
                availableItemsListAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void onCheckOutDoneClicked(View view){
        Intent moveToCheckoutOrder=new Intent(resturantsItemsAndDetails.this,CheckOutOrder.class);
        startActivity(moveToCheckoutOrder);
        //Toast.makeText(resturantsItemsAndDetails.this, "Nothing To do", Toast.LENGTH_SHORT).show();
    }
    private void addItemIntoHashMapCart(String selectedItemsName,double selectedItemprice) {
        if (cartOfItems.containsKey(selectedItemsName)){
            //if the item Already exists
            //just i want to update the quantity
            cartOfItems.put(selectedItemsName, cartOfItems.get(selectedItemsName) + 1);

        }else {
            cartOfItems.put(selectedItemsName,1);
            pricing.put(selectedItemsName,selectedItemprice);
        }
    }
}