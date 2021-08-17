package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class admin_Control_Panel extends AppCompatActivity {
    public DrawerLayout drawerMenuForAdmin;
    public BootstrapButton mangeEmployee,mangeItems,seeLatestOrder;
    public static String resturnatsId="",ownerId="";
    public void DefineAllObjects(){
        drawerMenuForAdmin=findViewById(R.id.drawerMainScreenForAdmin);
        mangeEmployee=findViewById(R.id.mangeEmployeeButton);
        mangeItems=findViewById(R.id.mangeItemsButton);
    }
    //Design Coding
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_control_panel);
        //Define Mirror Object For All Screen Content
        DefineAllObjects();
        //Get Actual Size For Screen used by User
        Display displayManeger=getWindowManager().getDefaultDisplay();
        Point displayPoint=new Point();
        displayManeger.getSize(displayPoint);
        int width=displayPoint.x;
        int height=displayPoint.y;
        //Get Admin Employee Id
        ProgressDialog createNewAdminDialog;
        createNewAdminDialog = new ProgressDialog(admin_Control_Panel.this);
        createNewAdminDialog.setMessage("Please Wait ... ");
        createNewAdminDialog.show();
        FirebaseDatabase.getInstance().getReference().child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Admin s=snapshot1.getValue(Admin.class);
                    if(s.adminEmail.equals(emolyeeLogins.emailofEmployee)) {
                        FirebaseDatabase.getInstance().getReference().child("Restaurant")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                        for(DataSnapshot snapshot123:snapshot2.getChildren()) {
                                            Resturants myBusinnes = snapshot123.getValue(Resturants.class);
                                            if(myBusinnes.restaurantOwnerId.equals(snapshot1.getKey())) {
                                                ownerId=snapshot1.getKey();
                                                resturnatsId=snapshot123.getKey();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("Error",error.getDetails());
            }
        });
    }
    // All the following method is special for manage the drawer menu
    /*
     * The first method is connected with more icon
     * we the user click on more icon (...)
     * The Special Drawer will Displayed */
    public void onMenuClicked(View view) {
        openDrawer(drawerMenuForAdmin);
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
    // Drawer menu Choices Methods
    public void OnHomeButtonCliked(View view){
        Intent backToHome=new Intent(getApplicationContext(),admin_Control_Panel.class);
        startActivity(backToHome);
    }
    public void OnSpecialOfferClicked(View view){
        Intent backToHome=new Intent(getApplicationContext(),addSpecialOffer.class);
        startActivity(backToHome);
    }
    public void OnLogoutClicked(View view){
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    public void onLatestOrderButtonClicked(View view) {
        String employeeId;
        Intent backToHome=new Intent(getApplicationContext(),latestOrder.class);
        startActivity(backToHome);
    }

    public void onManageItemsButtonClicked(View view) {
        String employeeId;
        Intent backToHome=new Intent(getApplicationContext(),manageItems.class);
        startActivity(backToHome);
    }

    public void onManageEmployeeButtonclicked(View view) {
        String employeeId;

        Intent backToHome=new Intent(getApplicationContext(),manageEmployee.class);
        startActivity(backToHome);
    }
}