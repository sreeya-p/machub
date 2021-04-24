package com.example.machub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SLOGIN extends AppCompatActivity {
    EditText e1,e2;
    Button b1;
    TextView t;
    SharedPreferences sh;
    String ip="192.168.43.174";
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_l_o_g_i_n);
        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        e1=(EditText)findViewById(R.id.editTextTextPersonName);
        e2=(EditText)findViewById(R.id.editTextTextPassword4);
        b1=(Button)findViewById(R.id.button2);
        t=(TextView)findViewById(R.id.textView10);
        SharedPreferences.Editor ed = sh.edit();
        ed.putString("ip", ip);
        ed.commit();


        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),shopreg.class);
                startActivity(i);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username=e1.getText().toString();
                final String password=e2.getText().toString();


                RequestQueue queue = Volley.newRequestQueue(SLOGIN.this);
                url = "http://" + ip + ":5000/login";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.d("+++++++++++++++++", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            String res = json.getString("task");
                            String arr[]=res.split("#");
                            SharedPreferences.Editor edp = sh.edit();
                            edp.putString("lid", arr[0]);
                            edp.commit();
                            if (arr[1].equalsIgnoreCase("invalid")) {
                                Toast.makeText(SLOGIN.this, "Invalid username or password", Toast.LENGTH_SHORT).show();

                            } else if(arr[1].equals("shop")){

                                Intent ik = new Intent(getApplicationContext(), ShopHome.class);
                                startActivity(ik);
                            }
                            else  if(arr[1].equals("staff"))
                            {
                                Intent ik = new Intent(getApplicationContext(), ShopHome.class);
                                startActivity(ik);
                            }
                            else
                            {
                                Toast.makeText(SLOGIN.this, "Invalid username or password", Toast.LENGTH_SHORT).show();

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
                        params.put("uname", username);
                        params.put("password", password);

                        return params;
                    }
                };
                queue.add(stringRequest);









            }
        });

    }
}