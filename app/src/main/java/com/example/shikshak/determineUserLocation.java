package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class determineUserLocation extends AppCompatActivity implements LocationListener {
    public TextView currentAddressView;
    public BootstrapButton fetchLocation,confirmLocation;
    public LocationManager myLocationManager;
    Intent getOrderDateToFillter;
    public double lat,lon;
    public static  String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_determine_user_location);
        //define Mirror Objects
        currentAddressView=findViewById(R.id.problemDescriptionLabel);
        fetchLocation=findViewById(R.id.fetchLocationButton);
        confirmLocation=findViewById(R.id.sendReportButton);
        confirmLocation.setVisibility(View.INVISIBLE);
        getOrderDateToFillter=getIntent();
        lat=0;
        lon=0;
        //Runtimes Permission
        if(ContextCompat.checkSelfPermission(determineUserLocation.this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(determineUserLocation.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        fetchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        confirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmLocation.setVisibility(View.INVISIBLE);
                updateUserLocation();
                Intent goTrack=new Intent(determineUserLocation.this,trackingOrderStatus.class);
                startActivity(goTrack);
            }
        });

    }
    private void updateUserLocation() {
        // UPDATE DATABASE START

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child("Orders");
        final Query gameQuery = ref.orderByChild("orderOwnerId").equalTo(MainActivity.usermail);

        gameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot childnow: snapshot.getChildren()) {
                        ShikOrder fetched=childnow.getValue(ShikOrder.class);
                        if(fetched.ordertimeandDate.equals(getOrderDateToFillter.getStringExtra("Date"))){
                            ref.child(childnow.getKey()).child("latitude").setValue(lat);
                            ref.child(childnow.getKey()).child("longtiude").setValue(lon);
                            date=getOrderDateToFillter.getStringExtra("Date");
                        }

                        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            myLocationManager=(LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,determineUserLocation.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(determineUserLocation.this, location.getLatitude()+""+location.getLongitude(), Toast.LENGTH_SHORT).show();
        lat=location.getLatitude();
        lon=location.getLongitude();
        try {
            Geocoder myGeocoder=new Geocoder(determineUserLocation.this, Locale.getDefault());
            List<Address> addressList=myGeocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String currentAddress=addressList.get(0).getAddressLine(0);
            currentAddressView.setText(currentAddress);
            confirmLocation.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}