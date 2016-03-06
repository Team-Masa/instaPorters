package com.instaporters.instaporters;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maheshkumar on 3/5/16.
 */
public class NavigateToWork extends Activity{
    private GoogleMap googleMap;
    ProgressDialog pDialog;
    List<LatLng> polyz;
    Double currentLat, currentLng, jobLat, jobLng;
    int jobId, porterId;
    Button statusButton;
    boolean start = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate_to_work);
        statusButton = (Button)findViewById(R.id.status);
        Intent intent = getIntent();
        currentLat = intent.getDoubleExtra("currentLat", 0.0);
        currentLng = intent.getDoubleExtra("currentLng", 0.0);
        jobLat = intent.getDoubleExtra("jobLat", 0.0);
        jobLng = intent.getDoubleExtra("jobLng", 0.0);
        jobId = intent.getIntExtra("jobId", 0);
        porterId = intent.getIntExtra("porterId", 0);
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start) {
                    //now stoppng
                    Log.d("stoppingthe job", "as");
                    stop_this_job(jobId, porterId);
                    Intent intent = new Intent(NavigateToWork.this, StopJob.class);
                    startActivity(intent);
                }else {
                    start = true;
                    //now starting
                    Log.d("starting the job", "as");
                    statusButton.setText(R.string.stop_now);
                    statusButton.setBackgroundColor(Color.parseColor("#c40b0b"));
                    start_this_job(jobId, porterId);
                }
            }
        });
        Log.d("ghgh", currentLat+";"+currentLng+"#"+jobLat+";"+jobLng);
        try{
            initializeMap();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void initializeMap() {
//        LatLng sourcePos = new LatLng(12.929648,77.6364297);
//        LatLng destination = new LatLng(12.9119126,77.6651401);
            if (googleMap == null){
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLng), 15));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
                googleMap.addMarker(new MarkerOptions().position(new LatLng(jobLat, jobLng)).title(getString(R.string.unload_goods)));
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    if (Build.VERSION.SDK_INT > 22)
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                googleMap.setMyLocationEnabled(true);
//                GMapV2Direction md = new GMapV2Direction();
//                Document doc = md.getDocument(sourcePos, destination, GMapV2Direction.MODE_DRIVING);
//                ArrayList<LatLng> directionpoint = md.getDirection(doc);
//                PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
//                Log.i("8998opop8998opop", directionpoint.size() + "00");
//                for (int i = 0; i < directionpoint.size(); i++) {
//                    rectLine.add(directionpoint.get(i));
//                    Log.i("opopopopop", directionpoint.get(i).toString() + " 90");
//                }
//                Polyline polyline = googleMap.addPolyline(rectLine);

                String serverKey = "AIzaSyC9BC7OsX_EaVWDMO73DA3F_P8m12wfNd0";
                LatLng origin = new LatLng(currentLat, currentLng);
                LatLng destination = new LatLng(jobLat, jobLng);
                GoogleDirection.withServerKey(serverKey)
                        .from(origin)
                        .to(destination)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction) {
                                String status = direction.getStatus();
                                if(direction.isOK()) {
                                    Log.d("INSTAXYZ", status);
                                    List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                                    ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getBaseContext(), stepList, 5, Color.RED, 3, Color.BLUE);
                                    for (PolylineOptions polylineOption : polylineOptionList) {
                                        googleMap.addPolyline(polylineOption);
                                    }
                                      Log.d("TESTINSTA",polylineOptionList.size()+"");// Do something
                                }else{
                                    Log.d("INSTAXYZ", status);
                                }
                                // Do something here

                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                // Do something here
                            }
                        });

                if (googleMap == null) {
                    Toast.makeText(getApplicationContext(), R.string.no_maps_sorry, Toast.LENGTH_LONG).show();
                }
            }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
    }
    void start_this_job(int jobId, int porterId) {
        JSONObject params = new JSONObject();
        try {
            params.put("porterId", porterId);
            params.put("jobId", jobId);
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiUrl.porter_start(), params,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), R.string.start_track, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
    void stop_this_job(int jobId, int porterId) {
        JSONObject params = new JSONObject();
        try {
            params.put("porterId", porterId);
            params.put("jobId", jobId);
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiUrl.porter_end(), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), R.string.finished_the_job, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
