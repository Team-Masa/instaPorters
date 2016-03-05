package com.instaporters.instaporters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maheshkumar on 3/5/16.
 */
public class JobLists extends Activity{
    RecyclerView recyclerView;
    CustomAdapter adapter;
    private List<FeedItem> feedsList;
    public static String[] title = {"t1", "t2", "t3", "t4"};
    public static String[] detail = {"d1", "d2", "d3", "d4"};
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_lists);
        recyclerView = (RecyclerView) findViewById(R.id.listview);
        context = this;
        feedsList = new ArrayList<>();
        for (int i = 0; i < title.length; i++){
            FeedItem item = new FeedItem();
            item.setTitle(title[i]);
            item.setDetail(detail[i]);
            feedsList.add(item);
        }
        adapter = new CustomAdapter(feedsList);
        Log.d("LOGGG", adapter.toString() + "Sas");
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }
}
