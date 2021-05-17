package com.example.machub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class add_service_reqst extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
Spinner s1;
EditText e1,e2;
Button b1;
SharedPreferences sh;
ArrayList<String> item,itemid;
String url,itid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_reqst);
        s1=findViewById(R.id.Spinner1);
        e1=findViewById(R.id.ed1);
        e2=findViewById(R.id.ed2);
        b1=findViewById(R.id.button13);
        s1.setOnItemSelectedListener(add_service_reqst.this);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getitems();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reqst=e1.getText().toString();
                String warnty=e2.getText().toString();
                String itm=s1.getSelectedItem().toString();
                if(itm.equals("select"))
                {
                    Toast.makeText(getApplicationContext(),"pls select item",Toast.LENGTH_LONG).show();
                }
                else if(reqst.equals(""))
                {
                    e1.setError("enter request");
                }
                else if(warnty.equals(""))
                {
                    e2.setError("enter warranty date");
                }

                else {
                    RequestQueue queue = Volley.newRequestQueue(add_service_reqst.this);
                    String url = "http://" + sh.getString("ip", "") + ":5000/service_reqst";

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
                            params.put("itemid", itid);
                            params.put("reqst", reqst);
                            params.put("warranty", warnty);
                            params.put("shpid", sh.getString("lid", ""));


                            return params;
                        }
                    };
// Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }

            }
        });

    }

    private void getitems() {
        url ="http://"+sh.getString("ip", "") + ":5000/allitems";
        RequestQueue queue = Volley.newRequestQueue(add_service_reqst.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);
                    item= new ArrayList<>();
                    itemid= new ArrayList<>();

                    item.add("select");
                    for(int i=1;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);
                        item.add(jo.getString("itemname"));
                        itemid.add(jo.getString("itemid"));



                    }
                    ArrayAdapter<String> ad=new ArrayAdapter<String>(add_service_reqst.this, android.R.layout.simple_list_item_1,item);
                     s1.setAdapter(ad);





                } catch (Exception e) {
                    Log.d("=========", e.toString());
                    Toast.makeText(add_service_reqst.this, e+"", Toast.LENGTH_SHORT).show();

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(add_service_reqst.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //  params.put("complaint","reply");


                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        itid=itemid.get(i);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
