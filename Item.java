package com.example.machub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Item extends AppCompatActivity implements AdapterView.OnItemClickListener  {
    ListView l1;
    SharedPreferences sp;
    String url="",ip="";
    ArrayList<String> itemid,itemname,rate,image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        l1=(ListView) findViewById(R.id.listview2);
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());



        url ="http://"+sp.getString("ip", "") + ":5000/view";
        RequestQueue queue = Volley.newRequestQueue(Item.this);

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


                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);
                        itemid.add(jo.getString("itemid"));
                        itemname.add(jo.getString("itemname"));
                        rate.add(jo.getString("rate"));
                        image.add(jo.getString("image"));



                    }

                    // ArrayAdapter<String> ad=new ArrayAdapter<>(Home.this,android.R.layout.simple_list_item_1,name);
                    //lv.setAdapter(ad);

                    l1.setAdapter(new Custom(Item.this,itemname,rate,image));

                    l1.setOnItemClickListener(Item.this);


                } catch (Exception e) {
                    Log.d("=========", e.toString());
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Item.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };
        queue.add(stringRequest);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long id){
        Intent in = new Intent(getApplicationContext(), ClipData.Item.class);
        in.putExtra("itemid", itemid.get(i));
        in.putExtra("itemname", itemname.get(i));
        in.putExtra("rate", rate.get(i));
        in.putExtra("image", image.get(i));

        startActivity(in);

    }
}
}