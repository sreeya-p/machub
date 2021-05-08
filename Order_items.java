package com.example.machub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Order_items extends AppCompatActivity {
    ListView l1;
    SharedPreferences sp;
    String url="",ip="",cid;
    Button b1;
    ArrayList<String> itemid,itemname,rate,image,qty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);
        l1=(ListView) findViewById(R.id.listview2);
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        url ="http://"+sp.getString("ip", "")+":5000/vieworderitems";
        RequestQueue queue = Volley.newRequestQueue(Order_items.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try
                {
                    JSONArray ar=new JSONArray(response);
                    if(ar.length()>0)
                    itemname= new ArrayList<>();
                    rate= new ArrayList<>();
                    image=new ArrayList<>();
                    qty=new ArrayList<>();
                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);

                        itemname.add(jo.getString("itemname"));
                        rate.add(jo.getString("tot_price"));
                        image.add(jo.getString("image"));
                        qty.add(jo.getString("qty"));

                    }

                    // ArrayAdapter<String> ad=new ArrayAdapter<>(Home.this,android.R.layout.simple_list_item_1,name);
                    //lv.setAdapter(ad);
                    l1.setAdapter(new customitem(Order_items.this,itemname,image,rate));



                } catch (Exception e) {
                    Toast.makeText(Order_items.this, "eeee"+e, Toast.LENGTH_SHORT).show();
                    Log.d("=========", e.toString());
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Order_items.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("oid",getIntent().getStringExtra("oid"));
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
