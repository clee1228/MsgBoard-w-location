package com.example.cs160_sp18.prog3;

import android.graphics.drawable.Drawable;
import android.location.Location;

import java.util.Comparator;

public class Landmarks {

    public String pic;
    public String name;
    public Location location;
    public String dist;


    Landmarks(String pic, String name, Location location, String dist){
        this.pic = pic;
        this.name = name;
        this.location = location;
        this.dist = dist;

    }

    public String getDist(){
        return dist;
    }



}
