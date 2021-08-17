package com.example.shikshak;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DeliveryMan {

    public String deliveryManName;
    public String deliveryManProfileImagePath;
    public String deliveryManHostRestaurantId;
    public String deliveryManEmail;
    public String deliveryManPhone;
    public String deliveryManPassword;

    public DeliveryMan(){

    }
    public DeliveryMan(String name,String imagePath,String resturantsId,
            String email,String phone,String password){
        this.deliveryManName=name;
        this.deliveryManProfileImagePath=imagePath;
        this.deliveryManHostRestaurantId=resturantsId;
        this.deliveryManEmail=email;
        this.deliveryManPhone=phone;
        this.deliveryManPassword=password;
    }
}
