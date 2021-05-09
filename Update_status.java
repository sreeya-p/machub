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
import android.widget.TextView;
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

public class Update_status extends AppCompatActivity {
    TextView e1,e2,e3,e4,e5,e6,e7,e8;
    Button b1;
    SharedPreferences sh;
    String url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);
        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        e1=findViewById(R.id.editTextTextPersonName2);
        e2=findViewById(R.id.editTextTextPersonName4);
        e3=findViewById(R.id.editTextTextPersonName5);
        e4=findViewById(R.id.editTextTextPersonName6);
        e5=findViewById(R.id.editTextTextPersonName7);
        b1=(Button)findViewById(R.id.button3);
        e1.setText(getIntent().getStringExtra("shop"));
        e2.setText(getIntent().getStringExtra("place"));
        e3.setText(getIntent().getStringExtra("post"));
        e4.setText(getIntent().getStringExtra("pin"));
        e5.setText(getIntent().getStringExtra("phone"));
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(Update_status.this);
                url = "http://" + sh.getString("ip", "") + ":5000/update_status";

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
                                Toast.makeText(Update_status.this, "success", Toast.LENGTH_SHORT).show();

                                Intent ik = new Intent(getApplicationContext(), deliveryagenthome.class);
                                startActivity(ik);

                            } else {

                                Toast.makeText(Update_status.this, "Invalid ", Toast.LENGTH_SHORT).show();

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
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("assid", getIntent().getStringExtra("oid"));
                        params.put("status", "delivered");



                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        });

    }
}
