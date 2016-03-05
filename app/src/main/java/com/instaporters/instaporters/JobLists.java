package com.instaporters.instaporters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
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
            feedsList.add(item);
        }
        adapter = new CustomAdapter(feedsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
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
                        startActivity(intent);
                        break;
                    case 4:
                        final FeedItem lastFeedRemoved = new FeedItem();
                        lastFeedRemoved.setTime(feedsList.get(position).getTime());
                        lastFeedRemoved.setPaymentPerPorter(feedsList.get(position).getPaymentPerPorter());
                        lastFeedRemoved.setLocDetails(feedsList.get(position).getLocDetails());
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
        JsonArrayRequest req = new JsonArrayRequest(ApiUrl.get_all_jobs(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String jsonResponse = "";
                    feedsList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        FeedItem thisItem = new FeedItem();
                        JSONObject jobObj = (JSONObject) response.get(i);
                        thisItem.setPaymentPerPorter(jobObj.getInt("paymentPerPorter"));
                        thisItem.setTime(jobObj.getString("time"));
                        thisItem.setLocDetails(jobObj.getJSONObject("location").getString("description"));
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
