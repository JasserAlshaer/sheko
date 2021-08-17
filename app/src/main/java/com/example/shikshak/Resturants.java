package com.example.shikshak;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Resturants {
    public String restaurantName;
    public String restaurantProfileImagePath;
    public String restaurantCategory;
    public int restaurantRate;
    public int restaurantNumberOfRating;

    public String restaurantOwnerId;
    public String avilableOffer;
    public Resturants(){
        //Define Default Constructor
    }
    public Resturants(String name,String imagepath,String category,String id,String offer,int rate,int numberofrate){
        this.restaurantName=name;
        this.restaurantProfileImagePath=imagepath;
        this.restaurantCategory=category;
        this.restaurantOwnerId=id;
        this.avilableOffer=offer;
        this.restaurantRate=rate;
        this.restaurantNumberOfRating=numberofrate;
    }

}
