package com.example.machub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class add_feadback extends AppCompatActivity {
EditText e;
Button b;
SharedPreferences sh;
String ip="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feadback);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        e=findViewById(R.id.editTextTextPersonName15);
        b=findViewById(R.id.button3);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String feadback=e.getText().toString();
                if(feadback.equalsIgnoreCase(""))
                {
                    e.setError("Enter your feadback ");
                }
                else {

                    RequestQueue queue = Volley.newRequestQueue(add_feadback.this);
                    String url = "http://" + sh.getString("ip", "") + ":5000/addfeadback";

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the response string.
                            Log.d("+++++++++++++++++", response);
                            try {
                                JSONObject json = new JSONObject(response);
                                String res = json.getString("task");

                                if (res.equalsIgnoreCase("success")) {
                                    Toast.makeText(getApplicationContext(), "sending success", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getApplicationContext(), ShopHome.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(getApplicationContext(), "sending failed", Toast.LENGTH_LONG).show();
//                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                                    startActivity(i);


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {


                            Toast.makeText(getApplicationContext(), "Error" + error, Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("feedback", feadback);
                            params.put("uid", sh.getString("lid", ""));


                            return params;
                        }
                    };
// Add the request to the RequestQueue.
                    queue.add(stringRequest);


                }



            }
        });

    }
}