package com.example.machub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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


public class accessories extends AppCompatActivity implements AdapterView.OnItemClickListener  {
    ListView l1;

    SharedPreferences sp;
    String url="",ip="";
    ArrayList<String> categoryid,category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        l1=(ListView) findViewById(R.id.listview1);
        l1.setOnItemClickListener(this);
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        url ="http://"+sp.getString("ip", "") + ":5000/viewproducts";
        RequestQueue queue = Volley.newRequestQueue(accessories.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);
                    categoryid= new ArrayList<>();
                    category= new ArrayList<>();

                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);
                        categoryid.add(jo.getString("accessoryid"));
                        category.add(jo.getString("accessory"));


                    }

                    ArrayAdapter<String> ad=new ArrayAdapter<>(accessories.this,android.R.layout.simple_list_item_1,category);
                    l1.setAdapter(ad);
                    l1.setOnItemClickListener(accessories.this);

//                        l1.setAdapter(new Custom(Category.this,category));
//                        l1.setOnItemClickListener(Category.this);

                } catch (Exception e) {
                    Log.d("=========", e.toString());
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(accessories.this, "err"+error, Toast.LENGTH_SHORT).show();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i=new Intent(getApplicationContext(),Category.class);
        SharedPreferences.Editor ed=sp.edit();
        ed.putString("aid",categoryid.get(position));
        ed.commit();
        startActivity(i);
    }
}
