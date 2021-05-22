package com.example.machub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class quantity extends AppCompatActivity {
    TextView t1;
    Spinner s;
    Button b1;
    SharedPreferences sh;
    String []qty={"select","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quantity);
        t1 = findViewById(R.id.textView51);
        s=findViewById(R.id.spinner4);
        b1=findViewById(R.id.button8);
        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ArrayAdapter<String> ad=new ArrayAdapter<String>(quantity.this,android.R.layout.simple_list_item_1,qty);
        s.setAdapter(ad);
        Toast.makeText(getApplicationContext(), "quantity added", Toast.LENGTH_LONG).show();
        b1.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                String quantity=s.getSelectedItem().toString();
                if (quantity.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "select quantity", Toast.LENGTH_LONG).show();
                }else {


                    RequestQueue queue = Volley.newRequestQueue(quantity.this);
                    String url = "http://" + sh.getString("ip", "") + ":5000/cartqty";

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
                                    Toast.makeText(getApplicationContext(), "quantity added", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getApplicationContext(), Cart_items.class);
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
                            params.put("quantity", quantity);
                            params.put("cid",getIntent().getStringExtra("cid"));


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