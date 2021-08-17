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
import android.view.Display;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class addorEditMangeItems extends AppCompatActivity {
    public CircleImageView profileImage;
    public BootstrapButton uploadImageChooise,ContinueAddItems;
    public BootstrapEditText NameField,DescriptionField,PriceField;
    public Uri ProfileImageUri;
    public Intent intent;
    public String deliveryManEmailInstnce;
    private void DefineAllScreenObject() {
        profileImage=findViewById(R.id.ItemImagePreview3);
        uploadImageChooise=findViewById(R.id.uploadProfileImage3);
        NameField=findViewById(R.id.itemsName);
        DescriptionField=findViewById(R.id.ItemsDescription);
        PriceField=findViewById(R.id.ItemsPrice);
        ContinueAddItems=findViewById(R.id.continueRegistrationButton3);
        ProfileImageUri=Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.items);
    }
    public void SelectImageItem(View view) {
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
    protected void ApplayResponsiveDesign(int width,int height){
        LinearLayout.LayoutParams RoundedImageDesign=new
                LinearLayout.LayoutParams((int)(width*0.325),(int)(height*0.185));
        RoundedImageDesign.setMargins(((int)(width*0.0225)),((int)(width*0.0125)),0,0);
        profileImage.setLayoutParams(RoundedImageDesign);

        LinearLayout.LayoutParams uploadButtonDesign=new
                LinearLayout.LayoutParams(((int)(width*0.510)),((int)(height*0.10)));
        uploadButtonDesign.setMargins((int)(width*0.0525),(int)(width*0.09),0, (int) (width*0.0311));
        uploadImageChooise.setTextSize((float) (width*0.02355));
        uploadImageChooise.setLayoutParams(uploadButtonDesign);

        LinearLayout.LayoutParams editTextDesign=new
                LinearLayout.LayoutParams((int)(width*0.9), (int)(height*0.0625));
        editTextDesign.setMargins((int)(width*0.05),(int)(width*0.045),0,0);
        NameField.setLayoutParams(editTextDesign);
        PriceField.setLayoutParams(editTextDesign);
        LinearLayout.LayoutParams descriptionField=new
                LinearLayout.LayoutParams((int)(width*0.9), (int)(height*0.1150));
        descriptionField.setMargins((int)(width*0.05),(int)(width*0.065),0,0);
        DescriptionField.setLayoutParams(descriptionField);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addor_edit_mange_items);
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
        intent=getIntent();
        if(!(intent.getStringExtra("type").equals("Add"))){
            profileImage.setVisibility(View.INVISIBLE);
            uploadImageChooise.setVisibility(View.INVISIBLE);
            // UPDATE DATABASE START
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference ref = database.child("Items");
            final Query queryToFetchInformation = ref.child(intent.getStringExtra("Id"));

            queryToFetchInformation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Items items=snapshot.getValue(Items.class);

                            NameField.setText(items.itemName);
                            DescriptionField.setText(items.itemdescreption);
                            PriceField.setText(items.itemPrice+"");

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            //UPDATE DATABASE END

        }
        //Insert new Items To Firebase Server
        ContinueAddItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent.getStringExtra("type").equals("Add")){
                    onContinueAddNewItems();
                }else{
                    onContinueEditItems(intent.getStringExtra("Id"));
                }
            }
        });
    }

    private void onContinueAddNewItems() {
        ProgressDialog createNewAdminDialog;
        createNewAdminDialog = new ProgressDialog(addorEditMangeItems.this);
        createNewAdminDialog.setMessage("Please Wait ... ");
        createNewAdminDialog.show();
        FirebaseStorage mfirebaseStorage=FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = mfirebaseStorage.getReference();

        StorageReference fileUploadingReference = storageRef.child(System.currentTimeMillis()+"."+getFileExtention(ProfileImageUri));
        fileUploadingReference.putFile(ProfileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileUploadingReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        if(ProfileImageUri == null || NameField.getText().toString().equals("") ||
                                DescriptionField.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(), "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
                        }else{
                            //After Image Is getting Then Create object then Upload data to firebase
                            Items newShikshakItem=new Items();
                            newShikshakItem.itemName=NameField.getText().toString();
                            newShikshakItem.itemImagePath=uri.toString();
                            newShikshakItem.itemHostResturantId=admin_Control_Panel.resturnatsId;
                            newShikshakItem.itemdescreption=DescriptionField.getText().toString();
                            newShikshakItem.itemPrice= PriceField.getText().toString();


                            FirebaseDatabase.getInstance().getReference().child("Items").push().setValue(newShikshakItem)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            createNewAdminDialog.dismiss();
                                            finish();
                                        }
                                    });
                            Toast.makeText(addorEditMangeItems.this, "Created Successfully Operation Success ", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(addorEditMangeItems.this, "Upload Operation Failed ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onContinueEditItems(String id) {
        // UPDATE DATABASE START

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Items").child(id).child("itemName").setValue(NameField.getText().toString());
        ref.child("Items").child(id).child("itemdescreption").setValue(DescriptionField.getText().toString());
        ref.child("Items").child(id).child("itemPrice").setValue(PriceField.getText().toString()+"");
        finish();

        }
    public String getFileExtention(Uri muri){
        ContentResolver mContentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(mContentResolver.getType(muri));
    }
}