package com.instaporters.instaporters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maheshkumar on 3/5/16.
 */
public class JobLists extends AppCompatActivity{
    RecyclerView recyclerView;
    CustomAdapter adapter;
    private List<FeedItem> feedsList;
    public static String[] title = {"t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8"};
    public static String[] detail = {"d1", "d2", "d3", "d4", "d5", "t6", "t7", "t8"};
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
            item.setTitle(title[i]);
            item.setDetail(detail[i]);
            feedsList.add(item);
        }
        adapter = new CustomAdapter(feedsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
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
                        lastFeedRemoved.setDetail(feedsList.get(position).getDetail());
                        lastFeedRemoved.setTitle(feedsList.get(position).getTitle());
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
}
