package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

public class manageEmployee extends AppCompatActivity {
    public ListView employeeList;
    public ArrayAdapter employeeListAdapter;
    public DrawerLayout manageEmployeeDrawerMenu;
    public ArrayList<DeliveryMan> myResturantEmployee;
    public ArrayList<String> myIds;
    public int myposition=0;
    public ArrayList<String> names;
    ProgressDialog createNewAdminDialog;

    public void DefineNecessaryObjectsAndVaribles(){
        myResturantEmployee=new ArrayList<DeliveryMan>();
        myIds=new ArrayList<String>();

        createNewAdminDialog = new ProgressDialog(manageEmployee.this);
        createNewAdminDialog.setMessage("Please Wait ... ");
        createNewAdminDialog.show();

        manageEmployeeDrawerMenu=findViewById(R.id.drawer_manage_employee_activity);
        employeeList=findViewById(R.id.listViewEmployeeImage);

        registerForContextMenu(employeeList);
        //Invoke The On Click Listener
        employeeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        employeeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                myposition=position;
                return false;
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_employee);
        names=new ArrayList<String>();
        DefineNecessaryObjectsAndVaribles();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateScreenContent();

    }

    private void updateScreenContent() {
        names.clear();
        myResturantEmployee.clear();
        myIds.clear();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseDatabase.getInstance().getReference().child("Delivery").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    DeliveryMan man=child.getValue(DeliveryMan.class);
                    if(man.deliveryManHostRestaurantId.equals(admin_Control_Panel.resturnatsId)){
                        names.add(man.deliveryManName);
                        myResturantEmployee.add(man);
                        myIds.add(child.getKey());
                    }
                }
                employeeListAdapter=new ArrayAdapter
                        (getApplicationContext(),R.layout.employeelist,R.id.employeeNameField,names){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view= super.getView(position, convertView, parent);
                        TextView employeeEmailTextView=view.findViewById(R.id.employeeEmailField);
                        employeeEmailTextView.setText(myResturantEmployee.get(position).deliveryManEmail);
                        CircleImageView imageView=view.findViewById(R.id.employeeProfileImage);
                        Glide.
                                with(getApplicationContext()).load(myResturantEmployee
                                .get(position).deliveryManProfileImagePath).into(imageView);
                        return view;
                    }
                };
                employeeList.setAdapter(employeeListAdapter);
                createNewAdminDialog.dismiss();
                employeeListAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // All the following method is special for manage the drawer menu
    /*
     * The first method is connected with more icon
     * we the user click on more icon (...)
     * The Special Drawer will Displayed */
    public void onMenuClicked(View view) {
        openDrawer(manageEmployeeDrawerMenu);
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
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId()==R.id.listViewEmployeeImage){
            AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("What Do You Want To Do ? :");
            String [] menuItem=getResources().getStringArray(R.array.options);
            for(int i=0;i<menuItem.length;i++){
                menu.add(Menu.NONE,i,i,menuItem[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex=item.getItemId();
        String [] menuItem=getResources().getStringArray(R.array.options);
        String menuItemName=menuItem[menuItemIndex];
        switch (menuItemIndex){

            case 0:
                goToEditEmployee();
                updateScreenContent();
                break;
            case 1:
                callPhoneNumber(myResturantEmployee.get(myposition).deliveryManPhone);
                break;
            case 2:
                deleteEmployee(myIds.get(myposition));
                updateScreenContent();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteEmployee(String s) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Delivery").child(s);

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

    public void OnAddNewEmployeeClicked(View view){
        Intent moveToAddNewEmployee=new Intent(getApplicationContext(),addOrEditEmployeeIinfo.class);
        moveToAddNewEmployee.putExtra("type","Add");
        startActivity(moveToAddNewEmployee);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber(myResturantEmployee.get(myposition).deliveryManPhone);
            }
        }
    }
    public void callPhoneNumber(String phone) {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(manageEmployee.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);

            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    protected  void goToEditEmployee(){
        Intent moveToAddNewEmployee=new Intent(getApplicationContext(),addOrEditEmployeeIinfo.class);
        moveToAddNewEmployee.putExtra("type","Edit");
        moveToAddNewEmployee.putExtra("Id",myIds.get(myposition));
        moveToAddNewEmployee.putExtra("ManEmail",myResturantEmployee.get(myposition).deliveryManEmail);
        startActivity(moveToAddNewEmployee);
    }
}