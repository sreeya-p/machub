package com.example.machub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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

public class  Item2 extends AppCompatActivity {
    Button b1;
    ImageView i1;
    TextView t1, t2, t3,t4,t5;
    SharedPreferences sh;
    Spinner s;
    String url = "", itemid, itemname, rate, description, image;
    String []qty={"select","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item2);
        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        i1 = findViewById(R.id.imageView2);
        b1 = findViewById(R.id.button5);
        t1 = findViewById(R.id.textView24);
        t2 = findViewById(R.id.textView25);
        t3 = findViewById(R.id.offers);
        t4 = findViewById(R.id.textView26);
        t5 = findViewById(R.id.textView31);
        s=findViewById(R.id.spinner3);
        itemid = getIntent().getStringExtra("itemid");
        itemname = getIntent().getStringExtra("itemname");
        rate = getIntent().getStringExtra("rate");
        description = getIntent().getStringExtra("description");
        image=getIntent().getStringExtra("image");
        String offers=getIntent().getStringExtra("offer");
        String offerrate=getIntent().getStringExtra("offer_rate");
//        String date=getIntent().getStringExtra("date");
        t1.setText(itemname);

        t4.setText(description);

        try {
            if(offers.equals("no"))
            {
                t3.setText("");
                t2.setText("");

            }
            else
            {
                t2.setText("OFFER RATE  "+offerrate+"/-");
                t3.setText(offers);
            }

        }
        catch (Exception e)
        {
            t3.setText("");
            t2.setText("");
        }
            t5.setText("ACTUAL PRICE  "+rate+"/-");


         ArrayAdapter<String> ad=new ArrayAdapter<String>(Item2.this,android.R.layout.simple_list_item_1,qty);
        s.setAdapter(ad);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        java.net.URL thumb_u;
        try {
            //thumb_u = new java.net.URL("http://192.168.43.57:5000/static/photo/flyer.jpg");
            thumb_u = new java.net.URL("http://" +sh.getString("ip", "") + ":5000/static/upload/" + image);
            Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
            i1.setImageDrawable(thumb_d);
        } catch (Exception e) {
            Log.d("errsssssssssssss", "" + e);
        }
   b1.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           double qtys=Double.parseDouble(s.getSelectedItem().toString());
           double totrate=qtys*Double.parseDouble(offerrate);
           if(String.valueOf(qtys).equals("select"))
           {
               Toast.makeText(getApplicationContext(),"pls select quantity",Toast.LENGTH_LONG).show();
           }
           else {
               RequestQueue queue = Volley.newRequestQueue(Item2.this);
               url = "http://" + sh.getString("ip", "") + ":5000/shop_cart";

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
                               Toast.makeText(Item2.this, "successfully added", Toast.LENGTH_SHORT).show();

                               Intent ik = new Intent(getApplicationContext(), Category.class);
                               startActivity(ik);

                           } else {

                               Toast.makeText(Item2.this, res, Toast.LENGTH_SHORT).show();

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
                       Map<String, String> params = new HashMap<String, String>();
                       params.put("itemid", itemid);
                       params.put("qty", "" + qtys);
                       params.put("rate", "" + totrate);
                       params.put("shpid", sh.getString("lid", ""));


                       return params;
                   }
               };
               queue.add(stringRequest);
           }


}

               });
    }}