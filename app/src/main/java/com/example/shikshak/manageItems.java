package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.widget.ListView;
import android.widget.TextView;

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

public class manageItems extends AppCompatActivity {
    public ListView ItemsList;
    public ArrayAdapter ItemsListAdapter;
    public DrawerLayout manageItemsDrawerMenu;
    public ArrayList<String> myItemsIds;
    public int myCurrentPosition=0;
    public ArrayList<String> ResturantsitemsName;
    public ArrayList<Items> itemsArrayList;
    public ProgressDialog createNewDialog;
    public void DefineNecessaryObjectsAndVaribles2(){
        itemsArrayList=new ArrayList<Items>();
        myItemsIds=new ArrayList<String>();
        createNewDialog = new ProgressDialog(manageItems.this);
        createNewDialog.setMessage("Please Wait ... ");
        createNewDialog.show();
        manageItemsDrawerMenu=findViewById(R.id.drawer_manage_items_activity);
        ItemsList=findViewById(R.id.listViewItemsMangement);

        registerForContextMenu(ItemsList);
        //Invoke The On Click Listener
        ItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myCurrentPosition=position;
            }
        });
        ItemsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);
        DefineNecessaryObjectsAndVaribles2();
        ResturantsitemsName=new ArrayList<String>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateScreenData();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId()==R.id.listViewItemsMangement){
            AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("What Do You Want To Do ? :");
            String [] menuItem=getResources().getStringArray(R.array.itemsOption);
            for(int i=0;i<menuItem.length;i++){
                menu.add(Menu.NONE,i,i,menuItem[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex=item.getItemId();
        String [] menuItem=getResources().getStringArray(R.array.itemsOption);
        String menuItemName=menuItem[menuItemIndex];
        switch (menuItemIndex){
            case 0:
                goToEditItems();
                updateScreenData();
                break;
            case 1:
                deleteItemsFromDatabase(myItemsIds.get(myCurrentPosition));
                updateScreenData();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void updateScreenData() {
        ResturantsitemsName.clear();
        itemsArrayList.clear();
        myItemsIds.clear();
        FirebaseApp.initializeApp(manageItems.this);
        FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    Items fetchedItem=child.getValue(Items.class);
                    if(fetchedItem.itemHostResturantId.equals(admin_Control_Panel.resturnatsId)){
                        ResturantsitemsName.add(fetchedItem.itemName);
                        itemsArrayList.add(fetchedItem);
                        myItemsIds.add(child.getKey());
                    }
                }
                ItemsListAdapter=new ArrayAdapter
                        (getApplicationContext(),R.layout.employeelist,R.id.employeeNameField,ResturantsitemsName){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view= super.getView(position, convertView, parent);
                        TextView employeeEmailTextView=view.findViewById(R.id.employeeEmailField);
                        employeeEmailTextView.setText(itemsArrayList.get(position).itemPrice+"");
                        CircleImageView imageView=view.findViewById(R.id.employeeProfileImage);
                        Glide.
                                with(getApplicationContext()).load(itemsArrayList
                                .get(position).itemImagePath).into(imageView);
                        return view;
                    }
                };
                ItemsList.setAdapter(ItemsListAdapter);
                createNewDialog.dismiss();
                ItemsListAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteItemsFromDatabase(String s) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Items").child(s);

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

    private void goToEditItems() {
        Intent moveToAddEditItems =new Intent(getApplicationContext(),addorEditMangeItems.class);
        moveToAddEditItems.putExtra("type","Edit");
        moveToAddEditItems.putExtra("Id",myItemsIds.get(myCurrentPosition));
        moveToAddEditItems.putExtra("UNK",myItemsIds.get(myCurrentPosition));
        startActivity(moveToAddEditItems);
    }

    // All the following method is special for manage the drawer menu
    /*
     * The first method is connected with more icon
     * we the user click on more icon (...)
     * The Special Drawer will Displayed */
    public void onMenuClicked(View view) {
        openDrawer(manageItemsDrawerMenu);
    }
    //This method do the opening drawer operation
    private static void openDrawer(DrawerLayout draw) {
        draw.openDrawer(GravityCompat.START);
    }
    //This method do the closing drawer operation
    private static void closeDrawer(DrawerLayout draw) {
        if(draw.isDrawerOpen(GravityCompat.START)){
            draw.closeDrawer(GravityCompat.START);
        }
    }
    public void onAddItemsClick(View view){
        Intent moveToAddNewItems=new Intent(getApplicationContext(),addorEditMangeItems.class);
        moveToAddNewItems.putExtra("type","Add");
        startActivity(moveToAddNewItems);
    }
}