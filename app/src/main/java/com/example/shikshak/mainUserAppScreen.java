package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class mainUserAppScreen extends AppCompatActivity {
    public DrawerLayout drawerMenuForUser;
    public ListView availableResturantsList;
    public ArrayAdapter resturantsListAdapter;
    public ArrayList<Resturants> avalibleResturantsObject,fillteredResturantsObject;
    public ArrayList<String> resturuantNames,restIds;
    public ProgressDialog createNewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user_app_screen);
        drawerMenuForUser=findViewById(R.id.drawerMainScreenForUser);
        availableResturantsList=findViewById(R.id.resturntsListView);
        avalibleResturantsObject=new ArrayList<Resturants>();
        fillteredResturantsObject=new ArrayList<Resturants>();
        resturuantNames=new ArrayList<String>();
        restIds=new ArrayList<String>();
        updateScreenData();
        availableResturantsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent moveToSelectItems=new Intent(mainUserAppScreen.this,resturantsItemsAndDetails.class);
                moveToSelectItems.putExtra("selectedId",restIds.get(position));
                startActivity(moveToSelectItems);
            }
        });
    }
    // All the following method is special for manage the drawer menu
    /*
     * The first method is connected with more icon
     * we the user click on more icon (...)
     * The Special Drawer will Displayed */
    public void onMenuClicked(View view) {
        openDrawer(drawerMenuForUser);
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
    private void updateScreenData() {
        createNewDialog = new ProgressDialog(mainUserAppScreen.this);
        createNewDialog.setMessage("Please Wait ... ");
        createNewDialog.show();
        fillteredResturantsObject.clear();
        avalibleResturantsObject.clear();
        resturuantNames.clear();
        restIds.clear();
        FirebaseApp.initializeApp(mainUserAppScreen.this);
        FirebaseDatabase.getInstance().getReference().child("Restaurant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    Resturants fetchedBusiness=child.getValue(Resturants.class);

                        resturuantNames.add(fetchedBusiness.restaurantName);
                        fillteredResturantsObject.add(fetchedBusiness);
                        avalibleResturantsObject.add(fetchedBusiness);
                        restIds.add(child.getKey());

                }

                resturantsListAdapter=new ArrayAdapter
                        (getApplicationContext(),R.layout.resturantsitems,R.id.ResturantsNameField,resturuantNames){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view= super.getView(position, convertView, parent);
                        CircleImageView myimage=view.findViewById(R.id.employeeProfileImage);
                        TextView categoryField=view.findViewById(R.id.employeeEmailField);
                        RatingBar mybar=view.findViewById(R.id.ratingBar1);
                        Glide.
                                with(getApplicationContext()).load(fillteredResturantsObject
                                .get(position).restaurantProfileImagePath).into(myimage);
                        categoryField.setText(fillteredResturantsObject.get(position).restaurantCategory);
                        int starNumber=fillteredResturantsObject.get(position).restaurantRate;
                        mybar.setNumStars(starNumber);
                        mybar.setVisibility(View.INVISIBLE);

                        return view;
                    }
                };
                availableResturantsList.setAdapter(resturantsListAdapter);
                  /*Glide.
                                with(getApplicationContext()).load(itemsArrayList
                                .get(position).itemImagePath).into(imageView);*/
                createNewDialog.dismiss();
                resturantsListAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void updateAvailableList(String selectedCategory){
        createNewDialog = new ProgressDialog(mainUserAppScreen.this);
        createNewDialog.setMessage("Please Wait ... ");
        createNewDialog.show();
        fillteredResturantsObject.clear();
        avalibleResturantsObject.clear();
        resturuantNames.clear();
        restIds.clear();
        FirebaseApp.initializeApp(mainUserAppScreen.this);
        FirebaseDatabase.getInstance().getReference().child("Restaurant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    Resturants fetchedBusiness=child.getValue(Resturants.class);
                    if(fetchedBusiness.restaurantCategory.equalsIgnoreCase(selectedCategory)){
                        resturuantNames.add(fetchedBusiness.restaurantName);
                        fillteredResturantsObject.add(fetchedBusiness);
                        avalibleResturantsObject.add(fetchedBusiness);
                        restIds.add(child.getKey());
                    }
                }
                createNewDialog.dismiss();
                resturantsListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void FillterCategoryOnApp(View view) {
        int textId=view.getId();
        TextView textView1=findViewById(R.id.categoryTextLabel1);
        TextView textView2=findViewById(R.id.categoryTextLabel2);
        TextView textView3=findViewById(R.id.categoryTextLabel3);
        TextView textView4=findViewById(R.id.categoryTextLabel4);
        switch (textId){
            case R.id.categoryTextLabel1:
                updateAvailableList(textView1.getText().toString());
                break;
            case R.id.categoryTextLabel2:
                updateAvailableList(textView2.getText().toString());
                break;
            case R.id.categoryTextLabel3:
                updateAvailableList(textView3.getText().toString());
                break;
            case R.id.categoryTextLabel4:
                updateAvailableList(textView4.getText().toString());
                break;
            case R.id.categoryTextLabel5:
               updateScreenData();
                break;
        }
    }

    public void UserSignOutButtonClicked(View view){
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}