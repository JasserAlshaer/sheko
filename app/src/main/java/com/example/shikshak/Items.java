package com.example.shikshak;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Items {
    public String itemName;
    public String itemImagePath;
    public String itemdescreption;
    public String itemHostResturantId;
    public String itemPrice;

    public Items(){

    }
    public Items(String name,String image,String description,String id,String price){
        this.itemName=name;
        this.itemImagePath=image;
        this.itemdescreption=description;
        this.itemHostResturantId=id;
        this.itemPrice=price;
    }
}
