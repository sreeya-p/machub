package com.example.machub;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

public class DeliveryReturn extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView l1;
    SharedPreferences sp;
    String url="",ip="",cid;
    ArrayList<String> itemid,itemname,rate,image,quantity,requestid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_return);
        l1=(ListView) findViewById(R.id.listview2);
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());




        url ="http://"+sp.getString("ip", "")+":5000/VIEWDELIVERYRETURN";
        RequestQueue queue = Volley.newRequestQueue(DeliveryReturn.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try
                {
                    JSONArray ar=new JSONArray(response);
                    itemid= new ArrayList<>();
                    itemname= new ArrayList<>();
                    rate= new ArrayList<>();
                    image=new ArrayList<>();
                    quantity=new ArrayList<>();
                    requestid=new ArrayList<>();

                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);

                        itemid.add(jo.getString("itemid"));
                        itemname.add(jo.getString("itemname"));
                        rate.add(jo.getString("tot_price"));
                        image.add(jo.getString("image"));
                        quantity.add(jo.getString("qty"));
                        requestid.add(jo.getString("returnorderid"));

                    }

                    // ArrayAdapter<String> ad=new ArrayAdapter<>(Home.this,android.R.layout.simple_list_item_1,name);
                    //lv.setAdapter(ad);
                    l1.setAdapter(new custom6(DeliveryReturn.this,itemname,image,rate,quantity));

                    l1.setOnItemClickListener(DeliveryReturn.this);


                } catch (Exception e) {
                    Toast.makeText(DeliveryReturn.this, "eeee"+e, Toast.LENGTH_SHORT).show();
                    Log.d("=========", e.toString());
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(DeliveryReturn.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("did",sp.getString("lid",""));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder ald=new AlertDialog.Builder(DeliveryReturn.this);
        ald.setTitle("Do you want to return")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        RequestQueue queue = Volley.newRequestQueue(DeliveryReturn.this);
                        String url = "http://" + sp.getString("ip", "") + ":5000/deliveryreturn";

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
                                        Toast.makeText(getApplicationContext(), "returned", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(getApplicationContext(),deliveryagenthome.class);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "cannot return", Toast.LENGTH_LONG).show();
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
                                params.put("rid", requestid.get(position));
                                params.put("itemid", itemid.get(position));
                                params.put("qty", quantity.get(position));



                                return params;
                            }
                        };
// Add the request to the RequestQueue.
                        queue.add(stringRequest);
                    }
                })
                .setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();

                    }
                });

        AlertDialog al=ald.create();
        al.show();

    }
}