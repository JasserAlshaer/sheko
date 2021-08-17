package com.example.shikshak;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@IgnoreExtraProperties

public class ShikOrder {

        public String orderOwnerId;
        public String orderDeliveryMan;
        public String orderStatus;
        public String orderComment;
        public String resturantId;
        public HashMap<String,Integer> cartofItemsFillingbyUser;
        public String ordertimeandDate;
        public int orderRatefromUser;
        public double latitude,longtiude;
        public String totalPrice;

        public ShikOrder(String owner,String delivery,String Status,String comment
                ,String Id,HashMap<String,Integer> cart,String time,int rate,double lat,double lon,String price){
            this.orderOwnerId=owner;
            this.orderDeliveryMan=delivery;
            this.orderStatus=Status;
            this.orderComment=comment;
            this.resturantId=Id;
            this.cartofItemsFillingbyUser=cart;
            this.ordertimeandDate=time;
            this.orderRatefromUser=rate;
            this.latitude=lat;
            this.longtiude=lon;
            this.totalPrice=price;
        }

    public ShikOrder() {

    }
}
