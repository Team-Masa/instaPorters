package com.instaporters.instaporters;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maheshkumar on 3/5/16.
 */
public class JobLists extends AppCompatActivity{
    RecyclerView recyclerView;
    CustomAdapter adapter;
    private List<FeedItem> feedsList;
    public static int[] title = {500};
    public static String[] detail = {"2016-03-05T19:11:46.410Z"};
    Context context;
    double longitude, latitude;

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().show();
        getSupportActionBar().setTitle("New Job offers");
        setContentView(R.layout.job_lists);
        recyclerView = (RecyclerView) findViewById(R.id.listview);
        context = this;
        feedsList = new ArrayList<>();
        for (int i = 0; i < title.length; i++){
            FeedItem item = new FeedItem();
            item.setPaymentPerPorter(title[i]);
            item.setTime("12");
            item.setLocDetails(detail[i]);
            item.setDistance(12.3);
            feedsList.add(item);
        }
        adapter = new CustomAdapter(feedsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT > 22)
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location!=null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }



        makeJsonArrayRequest();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case 8:
                        Intent intent = new Intent(JobLists.this, NavigateToWork.class);
                        intent.putExtra("currentLat",latitude);
                        intent.putExtra("currentLng",longitude);
                        intent.putExtra("jobLat",feedsList.get(position).getLat());
                        intent.putExtra("jobLng",feedsList.get(position).getLng());
                        startActivity(intent);
                        break;
                    case 4:
                        final FeedItem lastFeedRemoved = new FeedItem();
                        lastFeedRemoved.setTime(feedsList.get(position).getTime());
                        lastFeedRemoved.setPaymentPerPorter(feedsList.get(position).getPaymentPerPorter());
                        lastFeedRemoved.setLocDetails(feedsList.get(position).getLocDetails());
                        lastFeedRemoved.setDistance(feedsList.get(position).getDistance());
                        feedsList.remove(position);
                        recyclerView.getAdapter().notifyItemRemoved(position);
                        Snackbar.make(findViewById(R.id.listview), "Job rejected", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(findViewById(R.id.listview), "Job is restored!", Snackbar.LENGTH_LONG).show();
                                feedsList.add(position, lastFeedRemoved);
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }
                        }).show();
                        break;
                }

            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void makeJsonArrayRequest() {
        Log.d("psssse", longitude+";"+latitude);
//        double lat = 12.9312263;
//        double lon = 77.632554;
        double lat = latitude;
        double lon = longitude;
        Log.d("assign", longitude+";"+latitude);

        JsonArrayRequest req = new JsonArrayRequest(ApiUrl.get_all_jobs()+"lat="+lat+"&lng="+lon, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String jsonResponse = "";
                    feedsList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        FeedItem thisItem = new FeedItem();
                        JSONObject jobObj = (JSONObject) response.get(i);
                        thisItem.setPaymentPerPorter(jobObj.getInt("paymentPerPorter"));
                        thisItem.setTime(jobObj.getString("prettyDate"));
                        thisItem.setLocDetails(jobObj.getJSONObject("location").getString("description"));
                        thisItem.setDistance(jobObj.getDouble("distance"));
                        thisItem.setLat(jobObj.getJSONObject("location").getDouble("lat"));
                        thisItem.setLng(jobObj.getJSONObject("location").getDouble("lng"));
                        Log.d("iuytyui", jobObj.getDouble("distance")+ "AS");
                        feedsList.add(thisItem);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                    Log.d("dooone", feedsList.size() + "as");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = "";
                VolleyLog.d("RAG", "Error: " + error.getMessage());

                if (error instanceof NoConnectionError) {
                    errorMsg = "No internet Access, Check your internet connection.";
                } else {
                    errorMsg = error.getMessage();
                }
//                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

            }
        });
        AppController.getInstance().addToRequestQueue(req);
    }

}
