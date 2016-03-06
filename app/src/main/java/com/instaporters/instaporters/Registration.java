package com.instaporters.instaporters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by maheshkumar on 3/6/16.
 */
public class Registration extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        Button register = (Button)findViewById(R.id.register);
        final EditText name = (EditText)findViewById(R.id.name);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Please enter name!", Toast.LENGTH_LONG).show();
                    return;
                }
                regiter_user(name.getText().toString());
            }
        });

    }
    void regiter_user(String name) {
        JSONObject params = new JSONObject();
        try {
            params.put("name", name);
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiUrl.porters(), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int porterID = response.getInt("porterId");
                    Toast.makeText(getApplicationContext(), "Congratulation! you registered successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Registration.this, JobLists.class);
                    intent.putExtra("porterId", porterID);
                    startActivity(intent);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
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
