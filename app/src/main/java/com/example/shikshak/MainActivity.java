package com.example.shikshak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    public  CircleImageView mainImage;
    public ImageView drawerMenuIcon;
    public TextView actionbatTitle,emailLabel,passwordLabel,signupLabel,resetPassLabel;
    DrawerLayout mainActivityDrawerMenu;
    public BootstrapEditText emailField,passwordField;
    private FirebaseAuth mAuth;
    public static String userId,usermail;
    public void DefineAllScreenObject(){
        mainImage=findViewById(R.id.profile_image);
        actionbatTitle=findViewById(R.id.mainActionBarTitle);
        emailLabel=findViewById(R.id.emailTextView);
        passwordLabel=findViewById(R.id.passwordTextView);
        signupLabel=findViewById(R.id.createAccountTextview);
        resetPassLabel=findViewById(R.id.resetPassTextview);
        mainActivityDrawerMenu=findViewById(R.id.drawer_main_menu_activity);
        drawerMenuIcon=findViewById(R.id.menuMoreIcon);
        emailField=findViewById(R.id.emailTextField);
        passwordField=findViewById(R.id.passwordTextField);
    }
    public void ApplayResponsiveDesignforImage(int width,int height){

        LinearLayout.LayoutParams mainImageDesign=new
                LinearLayout.LayoutParams(width, (int) ((int)height*0.235));
        mainImageDesign.gravity= Gravity.CENTER;
        mainImage.setLayoutParams(mainImageDesign);
    }
    public void DesignForAppBarTitle(int width,int height){

        LinearLayout.LayoutParams TitlebarDesign=new
                LinearLayout.LayoutParams(width, (int) ((int)height*0.135));
        TitlebarDesign.gravity= Gravity.CENTER;
        actionbatTitle.setTextSize((float) (width*0.047));
    }
    public void setPrimaryTextSize(int width,int height){
        LinearLayout.LayoutParams TextSize=new
                LinearLayout.LayoutParams(width, (int) ((int)height*0.09250));
        TextSize.gravity= Gravity.CENTER;
        float size=(float) (width*0.0285);
        emailLabel.setTextSize(size);
        passwordLabel.setTextSize(size);
        signupLabel.setTextSize(size);
        resetPassLabel.setTextSize(size);
    }
    // All the following method is special for manage the drawer menu
    /*
     * The first method is connected with more icon
     * we the user click on more icon (...)
     * The Special Drawer will Displayed */
    public void onMenuClicked(View view) {
        openDrawer(mainActivityDrawerMenu);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Define All Object In Main Activity Screen
        DefineAllScreenObject();
        //Get Actual Size For Screen used by User
        Display displayManeger=getWindowManager().getDefaultDisplay();
        Point displayPoint=new Point();
        displayManeger.getSize(displayPoint);
        int width=displayPoint.x;
        int height=displayPoint.y;
        //Draw Image
        ApplayResponsiveDesignforImage(width,height);
        //Center Title for Login Screen
        DesignForAppBarTitle(width,height);
        //Set One Size for All Text In Screen
        setPrimaryTextSize(width,height);
        //test Data base



    }

    public void OnLoginButtonClicked(View view) {
        //get the email and password then Authenticate user using firebase
        mAuth = FirebaseAuth.getInstance();
        String userEmail,userPassword;
        userEmail=emailField.getText().toString();
        userPassword=passwordField.getText().toString();
        if(userEmail.equals("")||userPassword.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter Email And Password", Toast.LENGTH_SHORT).show();
        }else{
            //Continue Login Operation

       mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
                                    final Query gameQuery = ref.orderByChild("email").equalTo(userEmail);
                                    gameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot17) {
                                            if(snapshot17.exists()){
                                                for (DataSnapshot child: snapshot17.getChildren()) {
                                                    User classed=child.getValue(User.class);
                                                    if(classed.email.equals(userEmail)){
                                                        userId=child.getKey();
                                                        usermail=userEmail;
                                                    }
                                                }

                                                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                                Intent goToUserMainScreen=new
                                                        Intent(getApplicationContext(),mainUserAppScreen.class);

                                                startActivity(goToUserMainScreen);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }else{
                                    Toast.makeText(getApplicationContext(), "Please Verify Email", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i("Data", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }
    public void OnSignupLabelClicked(View view){
        //Move To Signup Screen
        Intent moveToSignupScreen=new Intent(MainActivity.this,create_account.class);
        startActivity(moveToSignupScreen);
    }
    public void OnResetPasswordLabelClicked(View view) {
        // Move To Reset Password Screen
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Enter Your New Password");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressDialog restPasswordProcessDialog;
                restPasswordProcessDialog = new ProgressDialog(MainActivity.this);
                restPasswordProcessDialog.setMessage("Please Wait Your Password Will Reset Now");
                restPasswordProcessDialog.show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.updatePassword(input.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    restPasswordProcessDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Done Successfully", Toast.LENGTH_SHORT).show();
                                }else{
                                    restPasswordProcessDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed Operation", Toast.LENGTH_SHORT).show();

                                }
                            }

                        });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    public  void LoginAsAdminOption(View view){
        // Login In AS Resturants Admin
        // There if user click yes we move to admin login via intent
        Intent moveToSuperVisor= new Intent(MainActivity.this,emolyeeLogins.class);
        moveToSuperVisor.putExtra("role","admin");
        startActivity(moveToSuperVisor);
    }
    public void LoginAsDeliveryManOption(View view){
        //Continue As Delivery Man
        Intent moveToSuperVisor= new Intent(MainActivity.this,emolyeeLogins.class);
        moveToSuperVisor.putExtra("role","delivery");
        startActivity(moveToSuperVisor);
    }
}