package com.example.machub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

public class BILL extends AppCompatActivity {
    TextView t1,t2,t3;
    ListView l1;
    SharedPreferences sp;
    String url="",ip="",cid;
    ArrayList<String> itemid,itemname,rate,image,qty;
    double tot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_i_l_l);
        t1 = findViewById(R.id.textView49);
        t2 = findViewById(R.id.textView44);
        t3 = findViewById(R.id.textView46);
        l1=(ListView) findViewById(R.id.listview3);
        t1.setText(getIntent().getStringExtra("oid"));
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        url ="http://"+sp.getString("ip", "")+":5000/billvieworderitems";
        RequestQueue queue = Volley.newRequestQueue(BILL.this);

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
                        tot+=Double.parseDouble(jo.getString("tot_price"));
                    }
                      t2.setText("Rs "+tot);
                    double gtotal=(tot+tot*(18/100));
                    t3.setText("Rs "+getIntent().getStringExtra("gtotal"));
                    // ArrayAdapter<String> ad=new ArrayAdapter<>(Home.this,android.R.layout.simple_list_item_1,name);
                    //lv.setAdapter(ad);
                    l1.setAdapter(new custom3(BILL.this,itemname,qty,rate));



                } catch (Exception e) {
                    Toast.makeText(BILL.this, "eeee"+e, Toast.LENGTH_SHORT).show();
                    Log.d("=========", e.toString());
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(BILL.this, "err"+error, Toast.LENGTH_SHORT).show();
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