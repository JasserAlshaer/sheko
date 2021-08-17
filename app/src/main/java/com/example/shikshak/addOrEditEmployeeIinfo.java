package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class addOrEditEmployeeIinfo extends AppCompatActivity {
    public CircleImageView profileImage;
    public BootstrapButton uploadImageChooise,ContinueSignupEmployee;
    public BootstrapEditText NameField,EmailField,PhoneField,PasswordField;
    public Uri ProfileImageUri;
    public Intent intent;
    public String deliveryManEmailInstnce;
    private void DefineAllScreenObject() {
        profileImage=findViewById(R.id.employeeImagePreview);
        uploadImageChooise=findViewById(R.id.uploadProfileImage2);
        NameField=findViewById(R.id.employeeName);
        EmailField=findViewById(R.id.employeeEmail);
        PhoneField=findViewById(R.id.employeePhone);
        PasswordField=findViewById(R.id.employeePassword);
        ContinueSignupEmployee=findViewById(R.id.continueRegistrationButton2);
    }
    protected void ApplayResponsiveDesign(int width,int height){
        LinearLayout.LayoutParams RoundedImageDesign=new
                LinearLayout.LayoutParams((int)(width*0.325),(int)(height*0.18));
        RoundedImageDesign.setMargins(((int)(width*0.0225)),((int)(height*0.0135)),0,0);
        profileImage.setLayoutParams(RoundedImageDesign);

        LinearLayout.LayoutParams uploadButtonDesign=new
                LinearLayout.LayoutParams(((int)(width*0.510)),((int)(height*0.085)));
        uploadButtonDesign.setMargins((int)(width*0.0525),(int) (height*0.0311),0, 0);
        uploadImageChooise.setTextSize((float) (width*0.02355));
        uploadImageChooise.setLayoutParams(uploadButtonDesign);

        LinearLayout.LayoutParams editTextDesign=new
                LinearLayout.LayoutParams((int)(width*0.9), (int)(height*0.0500));
        editTextDesign.setMargins((int)(width*0.05),(int)(height*0.0225),0,0);
        NameField.setLayoutParams(editTextDesign);
        EmailField.setLayoutParams(editTextDesign);
        PhoneField.setLayoutParams(editTextDesign);
        PasswordField.setLayoutParams(editTextDesign);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_employee_iinfo);
        //Define All Object In Main Activity Screen
        DefineAllScreenObject();
        //Get Actual Size For Screen used by User
        Display displayManeger=getWindowManager().getDefaultDisplay();
        Point displayPoint=new Point();
        displayManeger.getSize(displayPoint);
        int width=displayPoint.x;
        int height=displayPoint.y;
        //Mange The Design of Screen
        ProfileImageUri=Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.employee);
        ApplayResponsiveDesign(width,height);
         intent=getIntent();
         if(!(intent.getStringExtra("type").equals("Add"))){
            profileImage.setVisibility(View.INVISIBLE);
            uploadImageChooise.setVisibility(View.INVISIBLE);
            // UPDATE DATABASE START
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference ref = database.child("Delivery");
            final Query queryToFetchInformation = ref.orderByChild("deliveryManEmail").equalTo(intent.getStringExtra("ManEmail"));

            queryToFetchInformation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot child: snapshot.getChildren()) {
                            DeliveryMan man=child.getValue(DeliveryMan.class);
                            NameField.setText(man.deliveryManName);
                            EmailField.setText(man.deliveryManEmail);
                            deliveryManEmailInstnce=man.deliveryManEmail;
                            PhoneField.setText(man.deliveryManPhone);
                            PasswordField.setText(man.deliveryManPassword);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            //UPDATE DATABASE END

        }
        //Insert new Employee To Firebase Server
        ContinueSignupEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent.getStringExtra("type").equals("Add")){
                    onContinueAddNewEmployee();
                }else{
                    onContinueEditNewEmployee();
                }
            }
        });
    }
    public void SelectImageForDeliveryMan(View view) {
        Intent getImageFromGallery=new Intent();
        getImageFromGallery.setType("image/*");
        getImageFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(getImageFromGallery,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data.getData()!=null ){
            try {
                ProfileImageUri=data.getData();
                profileImage.setImageURI(ProfileImageUri);

            }catch (Exception exception){

            }
        }else {
            Toast.makeText(this, "Smothing Worng !", Toast.LENGTH_SHORT).show();
        }

    }
    //*********************************************************************************************
    public void onContinueAddNewEmployee() {
        ProgressDialog createNewAdminDialog;
        createNewAdminDialog = new ProgressDialog(addOrEditEmployeeIinfo.this);
        createNewAdminDialog.setMessage("Please Wait ... ");
        createNewAdminDialog.show();
        FirebaseStorage mfirebaseStorage=FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = mfirebaseStorage.getReference();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(EmailField.getText().toString(),PasswordField.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                    }
                });
        StorageReference fileUploadingReference = storageRef.child(System.currentTimeMillis()+"."+getFileExtention(ProfileImageUri));
        fileUploadingReference.putFile(ProfileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileUploadingReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        if(ProfileImageUri == null || NameField.getText().toString().equals("") ||
                        PasswordField.getText().toString().equals("") || EmailField.getText().toString().equals("")
                        || PhoneField.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(), "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
                        }else{
                            //After Image Is getting Then Create object then Upload data to firebase
                            DeliveryMan newShikshakDeliveryMan=new DeliveryMan();
                            newShikshakDeliveryMan.deliveryManName=NameField.getText().toString();
                            newShikshakDeliveryMan.deliveryManProfileImagePath=uri.toString();
                            newShikshakDeliveryMan.deliveryManHostRestaurantId=admin_Control_Panel.resturnatsId;
                            newShikshakDeliveryMan.deliveryManEmail=EmailField.getText().toString();
                            newShikshakDeliveryMan.deliveryManPhone=PhoneField.getText().toString();
                            newShikshakDeliveryMan.deliveryManPassword=PasswordField.getText().toString();

                            FirebaseDatabase.getInstance().getReference().child("Delivery").push().setValue(newShikshakDeliveryMan)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            finish();
                                        }
                                    });

                            Toast.makeText(addOrEditEmployeeIinfo.this, "Created Successfully Operation Success ", Toast.LENGTH_SHORT).show();
                            finish();
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
                Toast.makeText(addOrEditEmployeeIinfo.this, "Upload Operation Failed ", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void onContinueEditNewEmployee() {
        // UPDATE DATABASE START

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child("Delivery");
        final Query gameQuery = ref.orderByChild("deliveryManEmail").equalTo(deliveryManEmailInstnce);

        gameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot child: snapshot.getChildren()) {
                        ref.child(child.getKey()) .child("deliveryManName").setValue(NameField.getText().toString());
                        ref.child(child.getKey()) .child("deliveryManEmail").setValue(EmailField.getText().toString());
                        ref.child(child.getKey()) .child("deliveryManPhone").setValue(PhoneField.getText().toString());
                        ref.child(child.getKey()) .child("deliveryManPassword").setValue(PasswordField.getText().toString());
                        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //UPDATE DATABASE END
    }
    public String getFileExtention(Uri muri){
        ContentResolver mContentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(mContentResolver.getType(muri));
    }
}