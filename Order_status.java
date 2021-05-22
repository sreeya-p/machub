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
import android.widget.ArrayAdapter;
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

public class Order_status extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView l;
    String url;
    SharedPreferences sh;
    String ip="";
    ArrayList<String> oid,tot,gsttotal,status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        l=findViewById(R.id.listview);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        l.setOnItemClickListener(this);
        url ="http://"+sh.getString("ip", "") + ":5000/calculate";
        RequestQueue queue = Volley.newRequestQueue(Order_status.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the response string.
                Log.d("+++++++++++++++++",response);
                try {

                    JSONArray ar=new JSONArray(response);
                    oid= new ArrayList<>();
                    tot= new ArrayList<>();
                    gsttotal= new ArrayList<>();
                    status= new ArrayList<>();

                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject jo=ar.getJSONObject(i);
                        oid.add(jo.getString("oid"));
                        tot.add(jo.getString("total"));
                        gsttotal.add(jo.getString("gst"));
                        status.add(jo.getString("status"));



                    }


                    l.setAdapter(new Custom4(Order_status.this,oid,tot,gsttotal,status));



                } catch (Exception e) {
                    Log.d("=========", e.toString());
                    Toast.makeText(Order_status.this, e+"", Toast.LENGTH_SHORT).show();

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Order_status.this, "err"+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                  params.put("lid",sh.getString("lid",""));


                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AlertDialog.Builder ald=new AlertDialog.Builder(Order_status.this);
        ald.setTitle("Select your choice")
                .setPositiveButton(" View Bill ", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent ik=new Intent(getApplicationContext(),BILL.class);
                        ik.putExtra("oid",oid.get(i));
                        ik.putExtra("gtotal",gsttotal.get(i));
                        startActivity(ik);
                    }
                })
                .setNegativeButton(" Return Policy ", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent ik=new Intent(getApplicationContext(),Order_items.class);
                        ik.putExtra("oid",oid.get(i));
                        startActivity(ik);

                    }
                });

        AlertDialog al=ald.create();
        al.show();

    }

    }

