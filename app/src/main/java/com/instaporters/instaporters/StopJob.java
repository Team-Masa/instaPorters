package com.instaporters.instaporters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by maheshkumar on 3/6/16.
 */
public class StopJob extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_job);
        Button search = (Button) findViewById(R.id.moreJobs);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StopJob.this, JobLists.class);
                startActivity(intent);
            }
        });
    }
}
