package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class addNewResturants extends AppCompatActivity {
    public CircleImageView profileImage;
    public BootstrapButton uploadImageChooise,ContinueSignupBusiness;
    public BootstrapEditText NameField;
    public Spinner BusinnesType;
    public  String fetchIdbyemail,resturantsName,resturantsCategory;
    private Uri imageUri;
    Spinner selectAddress;
    ArrayAdapter spinnerAdapter;
    //Define Mirror Object For All Screen Content
    private void DefineAllScreenObject() {
        profileImage=findViewById(R.id.restaurantprofile_image);
        uploadImageChooise=findViewById(R.id.uploadProfileImage);
        NameField=findViewById(R.id.restaurantName);
        ContinueSignupBusiness=findViewById(R.id.continueRegistrationButton);
        BusinnesType=findViewById(R.id.BusinessCategoryList);
    }
    protected void ApplayResponsiveDesign(int width,int height){
        LinearLayout.LayoutParams RoundedImageDesign=new
                LinearLayout.LayoutParams((int)(width*0.325),(int)(height*0.20));
        RoundedImageDesign.setMargins(((int)(width*0.0225)),((int)(width*0.0125)),0,0);
        profileImage.setLayoutParams(RoundedImageDesign);

        LinearLayout.LayoutParams uploadButtonDesign=new
                LinearLayout.LayoutParams(((int)(width*0.510)),((int)(height*0.10)));
        uploadButtonDesign.setMargins((int)(width*0.0525),(int)(height*0.0245),0, (int) (width*0.0311));
        uploadImageChooise.setTextSize((float) (width*0.02355));
        uploadImageChooise.setLayoutParams(uploadButtonDesign);

        LinearLayout.LayoutParams editTextDesign=new
                LinearLayout.LayoutParams((int)(width*0.9), (int)(height*0.0625));
        editTextDesign.setMargins((int)(width*0.05),(int)(width*0.09),0,0);
        BusinnesType.setLayoutParams(editTextDesign);
        NameField.setLayoutParams(editTextDesign);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_resturants);
        FirebaseApp.initializeApp(this);
        //Define All Object In Main Activity Screen
        DefineAllScreenObject();
        //Get Actual Size For Screen used by User
        Display displayManeger=getWindowManager().getDefaultDisplay();
        Point displayPoint=new Point();
        displayManeger.getSize(displayPoint);
        int width=displayPoint.x;
        int height=displayPoint.y;
        //Mange The Design of Screen
        ApplayResponsiveDesign(width,height);
        //Define Spinner Adapter
        String [] categorySpinnerItems=getResources().getStringArray(R.array.restaurantsCategory);
        ArrayAdapter spinnerAdapter =
                new ArrayAdapter(this, android.R.layout.simple_spinner_item,categorySpinnerItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BusinnesType.setAdapter(spinnerAdapter);
        BusinnesType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resturantsCategory=categorySpinnerItems[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                resturantsCategory = "";
            }
        });

        // Coding to add New Restaurants
        Intent reciverData=getIntent();
        String adminEmail=reciverData.getStringExtra("AdminEmail");
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().getReference().child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Admin s=snapshot1.getValue(Admin.class);
                    if(s.adminEmail.equals(adminEmail)) {
                        fetchIdbyemail = snapshot1.getKey();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("Error",error.getDetails());
            }
        });
        uploadImageChooise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImageFromGallery=new Intent();
                getImageFromGallery.setType("image/*");
                getImageFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(getImageFromGallery,1);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data.getData()!=null ){
            try {
                imageUri=data.getData();
                profileImage.setImageURI(imageUri);

            }catch (Exception exception){

            }
        }else {
            Toast.makeText(this, "Smothing Worng !", Toast.LENGTH_SHORT).show();
        }

    }

    public void onContinueAddResturants(View view) {
        ProgressDialog createNewAdminDialog;
        createNewAdminDialog = new ProgressDialog(addNewResturants.this);
        createNewAdminDialog.setMessage("Please Wait ... ");
        createNewAdminDialog.show();
            FirebaseStorage mfirebaseStorage=FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = mfirebaseStorage.getReference();

            StorageReference fileUploadingReference = storageRef.child(System.currentTimeMillis()+"."+getFileExtention(imageUri));
            fileUploadingReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileUploadingReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            /*
                             * Modle modle=new Modle(uri.toString());
                             * String modleKey=root.Push.getKey();
                             * root.child(modlekey)
                             * */
                            resturantsName=NameField.getText().toString();
                            if(imageUri == null || resturantsName.equals("") || resturantsCategory.equals("") ){
                                Toast.makeText(getApplicationContext(), "Enter Valid Data", Toast.LENGTH_SHORT).show();
                            }else{
                                //After Image Is getting Then Create object then Upload data to firebase
                                Resturants addNewResturantsonShikShak=new Resturants();
                                addNewResturantsonShikShak.restaurantOwnerId=fetchIdbyemail;
                                addNewResturantsonShikShak.avilableOffer="null";
                                addNewResturantsonShikShak.restaurantRate=0;
                                addNewResturantsonShikShak.restaurantNumberOfRating=0;
                                addNewResturantsonShikShak.restaurantName=resturantsName;
                                addNewResturantsonShikShak.restaurantCategory=resturantsCategory;
                                addNewResturantsonShikShak.restaurantProfileImagePath=uri.toString();

                                FirebaseDatabase.getInstance().getReference().child("Restaurant").push().setValue(addNewResturantsonShikShak)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                createNewAdminDialog.dismiss();
                                                Intent backToHome=new Intent(addNewResturants.this,MainActivity.class);
                                                startActivity(backToHome);
                                            }
                                        });
                                Toast.makeText(addNewResturants.this, "Created Successfully Operation Success ", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(addNewResturants.this, "Upload Operation Failed ", Toast.LENGTH_SHORT).show();
                }
            });

    }
    public String getFileExtention(Uri muri){
        ContentResolver mContentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(mContentResolver.getType(muri));
    }
}