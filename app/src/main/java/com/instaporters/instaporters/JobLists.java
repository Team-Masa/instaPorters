package com.instaporters.instaporters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Created by maheshkumar on 3/5/16.
 */
public class JobLists extends Activity{
    ListView listView;
    public static String[] title = {"t1", "t2", "t3", "t4"};
    public static String[] detail = {"d1", "d2", "d3", "d4"};
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_lists);
        listView = (ListView) findViewById(R.id.listview);
        context = this;

        listView.setAdapter(new CustomAdapter(this, title, detail));

    }
}
