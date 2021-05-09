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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Works extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView l;
    String url;
    SharedPreferences sh;
    String ip="";
    ArrayList<String> oid,tot,date,shname,phn,place,post,pin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works);
        l=findViewById(R.id.listview);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        l.setOnItemClickListener(this);
        url ="http://"+sh.getString("ip", "") + ":5000/viewassigned";
        RequestQueue queue = Volley.newRequestQueue(Works.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);
                    oid= new ArrayList<>();
                    tot= new ArrayList<>();
                    date= new ArrayList<>();
                    shname= new ArrayList<>();
                    place= new ArrayList<>();
                    post= new ArrayList<>();
                    pin= new ArrayList<>();
                    phn= new ArrayList<>();

                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);
                        oid.add(jo.getString("asid"));
                        tot.add(jo.getString("total"));
                        date.add(jo.getString("date"));
                        shname.add(jo.getString("shopname"));
                        place.add(jo.getString("place"));
                        post.add(jo.getString("post"));
                        pin.add(jo.getString("pin"));
                        phn.add(jo.getString("phone"));



                    }


                    l.setAdapter(new custom3(Works.this,oid,tot,date));



                } catch (Exception e) {
                    Log.d("=========", e.toString());
                    Toast.makeText(Works.this, e+"", Toast.LENGTH_SHORT).show();

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Works.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("agid",sh.getString("lid",""));


                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        AlertDialog.Builder ald=new AlertDialog.Builder(Works.this);
        ald.setTitle("select")
                .setPositiveButton("Update status", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent ik = new Intent(getApplicationContext(), Update_status.class);
                        ik.putExtra("shop",shname.get(i));
                        ik.putExtra("place",place.get(i));
                        ik.putExtra("post",post.get(i));
                        ik.putExtra("pin",pin.get(i));
                        ik.putExtra("phone",phn.get(i));
                        ik.putExtra("oid",oid.get(i));
                        startActivity(ik);

                    }
                })
                .setNegativeButton(" View Items ", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent ik = new Intent(getApplicationContext(), Order_items.class);
                        ik.putExtra("oid",oid.get(i));
                        startActivity(ik);

                    }
                });

        AlertDialog al=ald.create();
        al.show();



    }
}
