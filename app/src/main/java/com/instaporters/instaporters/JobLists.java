package com.instaporters.instaporters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

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
    int porterId, distance;

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().show();
        getSupportActionBar().setTitle(R.string.new_job_offers);
        setContentView(R.layout.job_lists);
        porterId = getIntent().getIntExtra("porterId", 12);
        Log.d("Porterid", porterId + " o");
        recyclerView = (RecyclerView) findViewById(R.id.listview);
        context = this;
        feedsList = new ArrayList<>();
        for (int i = 0; i < title.length; i++){
            FeedItem item = new FeedItem();
            item.setPaymentPerPorter(title[i]);
            item.setTime("12");
            item.setJobId(1);
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
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (dX > 0) {
                    Log.d("Posit", "pp;  " +dX + "," +dY);
                    viewHolder.itemView.setBackgroundColor(Color.GREEN);
                }else if (dX < 0){
                    Log.d("Posit", "pp;  " +dX + "," +dY);
                    viewHolder.itemView.setBackgroundColor(Color.RED);
                }else
                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#4dFFFFFF"));

            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case 8:
                        acceptJob(feedsList.get(position).getJobId(), porterId, feedsList.get(position).getDistance());
                        Intent intent = new Intent(JobLists.this, NavigateToWork.class);
                        intent.putExtra("jobId",feedsList.get(position).getJobId());
                        intent.putExtra("currentLat",latitude);
                        intent.putExtra("porterId", porterId);
                        intent.putExtra("currentLng",longitude);
                        intent.putExtra("jobLat",feedsList.get(position).getLat());
                        intent.putExtra("jobLng",feedsList.get(position).getLng());
                        startActivity(intent);
                        break;
                    case 4:
                        final FeedItem lastFeedRemoved = new FeedItem();
                        lastFeedRemoved.setJobId(feedsList.get(position).getJobId());
                        lastFeedRemoved.setTime(feedsList.get(position).getTime());
                        lastFeedRemoved.setPaymentPerPorter(feedsList.get(position).getPaymentPerPorter());
                        lastFeedRemoved.setLocDetails(feedsList.get(position).getLocDetails());
                        lastFeedRemoved.setDistance(feedsList.get(position).getDistance());
                        feedsList.remove(position);
                        recyclerView.getAdapter().notifyItemRemoved(position);
                        Snackbar.make(findViewById(R.id.listview), R.string.job_rejeected, Snackbar.LENGTH_LONG).setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(findViewById(R.id.listview), R.string.job_restored, Snackbar.LENGTH_LONG).show();
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
    void acceptJob(int jobId, int porterId, double distance) {
        JSONObject params = new JSONObject();
        try {
            params.put("porterId", porterId);
            params.put("jobId", jobId);
            params.put("distance", distance);
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiUrl.assign_job_to_porter(), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), R.string.job_is_yours, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
    private void makeJsonArrayRequest() {
        Log.d("psssse", longitude+";"+latitude);
//        double lat = 12.9312263;
//        double lon = 77.632554;
        double lat = latitude;
        double lon = longitude;
        Log.d("assign", longitude+";"+latitude);

        JsonArrayRequest req = new JsonArrayRequest(ApiUrl.get_all_jobs()+"lat="+lat+"&lng="+lon+"&porterId="+porterId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String jsonResponse = "";
                    feedsList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        FeedItem thisItem = new FeedItem();
                        JSONObject jobObj = (JSONObject) response.get(i);
                        thisItem.setJobId(jobObj.getInt("jobId"));
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
                    errorMsg = getString(R.string.no_internet);
                } else {
                    errorMsg = error.getMessage();
                }
//                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

            }
        });
        AppController.getInstance().addToRequestQueue(req);
    }
    public void fadeTutorial(View view) {
        FrameLayout tutrial_overlay = (FrameLayout) findViewById(R.id.tutrial_overlay);
        tutrial_overlay.setVisibility(View.GONE);
    }

}
