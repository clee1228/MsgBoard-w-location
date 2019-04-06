package com.example.cs160_sp18.prog3;

import android.content.Context;
import android.content.Intent;

import android.location.LocationManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.location.Location;
import android.location.LocationListener;
import android.Manifest;


import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SecondActivity extends AppCompatActivity implements LocationListener {

    public static String username;
    public String longitude, latitude;
    public LocationManager locationManager;
    private Menu myMenu;
    boolean setUser = false;
    RecyclerView recyclerView;
    LandmarkAdapter landmarkAdapter;
    public ArrayList<Landmarks> landmarks;
    public Comparator<Landmarks> comparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);


        Intent i = getIntent();
        username = i.getStringExtra("username");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

        recyclerView = findViewById(R.id.mRecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        writeJSON(loadJSONFromAsset(this));


        Collections.sort(landmarks, new LandmarkComparator());

        setAdapterAndUpdateData();

    }




    public String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("bears.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void writeJSON(String result) {
        try {
            JSONArray json = new JSONArray(result);
            landmarks = new ArrayList<Landmarks>();
            for (int i = 0; i < json.length(); i++) {
                JSONObject json_data = json.getJSONObject(i);
                Landmarks landmark = new Landmarks(null, null, null, null);

                landmark.name = json_data.getString("landmark_name");
                landmark.pic = json_data.getString("filename");
                String coords = json_data.getString("coordinates");
                String[] location = coords.split(",");
                String lat = location[0];
                String longi = location[1];

                Location loc = new Location(landmark.name);
                loc.setLatitude(Double.parseDouble(lat));
                loc.setLongitude(Double.parseDouble(longi));


                Location curr = new Location("curr");
                curr.setLatitude(Double.parseDouble(this.latitude));
                curr.setLongitude(Double.parseDouble(this.longitude));


                float dist = curr.distanceTo(loc);

                if (Math.round(dist) < 10) {
                    landmark.dist = "Less than 10 meters away";
                } else {
                    landmark.dist = String.valueOf(Math.round(dist)) + " meters away";
                }

                landmarks.add(landmark);
            }
        } catch (JSONException e) {
            Toast.makeText(SecondActivity.this, e.toString(), Toast.LENGTH_LONG).show();

        }
    }


    public void getLocation() {
        Location location;
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double lat = location.getLatitude();
                double longi = location.getLongitude();
                this.latitude = String.valueOf(lat);
                this.longitude = String.valueOf(longi);

            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        this.myMenu = menu;
        int index = username.indexOf('@');
        String showUser = username.substring(0, index);
        menu.findItem(R.id.currUser).setTitle("Welcome, " + showUser);
        return true;
    }

    public void signOut(){
        Log.d("tag", "sign out");
        FirebaseAuth.getInstance().signOut();
        Intent signout = new Intent(SecondActivity.this, MainActivity.class);
        startActivity(signout);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout){
            signOut();
        }


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
            writeJSON(loadJSONFromAsset(this));
            setAdapterAndUpdateData();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void setAdapterAndUpdateData() {

        landmarkAdapter = new LandmarkAdapter(this, landmarks);
        recyclerView.setAdapter(landmarkAdapter);
        recyclerView.smoothScrollToPosition(0);
    }




}








