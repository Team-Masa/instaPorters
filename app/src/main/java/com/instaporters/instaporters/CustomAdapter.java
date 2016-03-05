package com.instaporters.instaporters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by maheshkumar on 3/5/16.
 */
public class CustomAdapter extends BaseAdapter {
    String [] title;
    String [] detail;
    Context context;
    private static LayoutInflater inflater = null;
    public CustomAdapter(JobLists jobLists, String[] title, String[] detail) {
        this.title = title;
        this.detail = detail;
        context = jobLists;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return title.length;
    }
    public class Holder {
        TextView title;
        TextView detail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder= new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.jobs_details, null);
        holder.title = (TextView) rowView.findViewById(R.id.title);
        holder.detail = (TextView) rowView.findViewById(R.id.detail);
        holder.title.setText(title[position]);
        holder.detail.setText(detail[position]);
        return rowView;
    }
}
