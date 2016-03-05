package com.instaporters.instaporters;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maheshkumar on 3/5/16.
 */
public class NavigateToWork extends Activity{
    private GoogleMap googleMap;
    ProgressDialog pDialog;
    List<LatLng> polyz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate_to_work);
        try{
            initializeMap();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void initializeMap() {
        LatLng sourcePos = new LatLng(12.929648,77.6364297);
        LatLng destination = new LatLng(12.9119126,77.6651401);
            if (googleMap == null){
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.929648, 77.6364297), 15));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    if (Build.VERSION.SDK_INT > 22)
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                googleMap.setMyLocationEnabled(true);
                GMapV2Direction md = new GMapV2Direction();
                Document doc = md.getDocument(sourcePos, destination, GMapV2Direction.MODE_DRIVING);
                ArrayList<LatLng> directionpoint = md.getDirection(doc);
                PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
                Log.i("8998opop8998opop", directionpoint.size() + "00");
                for (int i = 0; i < directionpoint.size(); i++) {
                    rectLine.add(directionpoint.get(i));
                    Log.i("opopopopop", directionpoint.get(i).toString() + " 90");
                }
                Polyline polyline = googleMap.addPolyline(rectLine);

                if (googleMap == null) {
                    Toast.makeText(getApplicationContext(), "No maps sorry", Toast.LENGTH_LONG).show();
                }
            }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
    }
}
