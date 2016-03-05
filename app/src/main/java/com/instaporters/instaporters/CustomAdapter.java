package com.instaporters.instaporters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by maheshkumar on 3/5/16.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>{
    private List<FeedItem> feedItemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, detail, time;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            detail = (TextView) view.findViewById(R.id.detail);
            time = (TextView) view.findViewById(R.id.time);
        }

    }
    public CustomAdapter(List<FeedItem> feeds) {
        this.feedItemList = feeds;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobs_details, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FeedItem item = feedItemList.get(position);
        holder.title.setText(Integer.toString(item.getPaymentPerPorter()));
        holder.detail.setText(item.getLocDetails());
        holder.time.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }

}