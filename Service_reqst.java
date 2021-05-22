package com.example.machub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class Service_reqst extends AppCompatActivity {
    ListView l;
    String url;
    SharedPreferences sh;
    String ip="";
    Button b1;
    ArrayList<String> item,reqst,reply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_reqst);
        l=findViewById(R.id.listview);
        b1=findViewById(R.id.but);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        url ="http://"+sh.getString("ip", "") + ":5000/shopviewreply";
        RequestQueue queue = Volley.newRequestQueue(Service_reqst.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);
                    item= new ArrayList<>();
                    reqst= new ArrayList<>();
                    reply= new ArrayList<>();

                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);
                        item.add(jo.getString("itemname"));
                        reqst.add(jo.getString("request"));
                        reply.add(jo.getString("reply"));


                    }
                    //   ArrayAdapter<String>ad=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,complaint);
                    // l.setAdapter(ad);


                    //   lv.setAdapter(ad);
                    l.setAdapter(new custom3(Service_reqst.this,item,reqst,reply));
//                    l.setOnItemClickListener(reply.this);

                } catch (Exception e) {
                    Log.d("=========", e.toString());
                    Toast.makeText(Service_reqst.this, e+"", Toast.LENGTH_SHORT).show();

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Service_reqst.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //  params.put("complaint","reply");
                params.put("uid",sh.getString("lid",""));

                return params;
            }
        };
        queue.add(stringRequest);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ik=new Intent(getApplicationContext(),add_service_reqst.class);
                startActivity(ik);

            } });
    }
}
