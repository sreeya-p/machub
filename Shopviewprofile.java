package com.example.machub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Shopviewprofile extends AppCompatActivity {

        EditText e1, e2, e3, e4,e5,e6;
        Button b1;
        String url;
        SharedPreferences sh;
        String shopname,phone,place,post,pincode,email;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_shopviewprofile);
            e1 =findViewById(R.id.editTextTextPersonName9);
            e2 =findViewById((R.id.editTextTextPersonName10));
            e3 =findViewById((R.id.editTextTextPersonName11));
            e4 =findViewById(R.id.editTextTextPersonName12);
            e5 =findViewById(R.id.editTextTextPersonName13);
            e6 =findViewById(R.id.editTextTextPersonName14);
//        e5 = (EditText) findViewById(R.id.editTextTextPassword);
//        e6 = (EditText) findViewById(R.id.editTextTextPassword2);
            b1 =findViewById(R.id.button4);
            sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            RequestQueue queue = Volley.newRequestQueue(Shopviewprofile.this);
            url ="http://"+sh.getString("ip", "")+":5000/viewprofile";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the response string.
                    Log.d("+++++++++++++++++",response);
                    try {
                        JSONArray ar=new JSONArray(response);
//                    name= new ArrayList<>();
//                    address= new ArrayList<>();
//                    phone= new ArrayList<>();
//                    password=new ArrayList<>();
//                    confirmpass=new ArrayList<>();

//                    for(int i=0;i<ar.length();i++)
//                    {
                        JSONObject jo=ar.getJSONObject(0);
                        e1.setText(jo.getString("shopname"));
                        e2.setText(jo.getString("phone"));
                        e3.setText(jo.getString("place"));
                        e4.setText(jo.getString("post"));
                        e5.setText(jo.getString("pin"));
                        e6.setText(jo.getString("email"));
//                        e5.setText(jo.getString("password"));
//                        e6.setText(jo.getString("confirmpass"));

//                    }

                        // ArrayAdapter<String> ad=new ArrayAdapter<>(Home.this,android.R.layout.simple_list_item_1,name);
                        //lv.setAdapter(ad);

//                    l1.setAdapter(new Custom(myprofile.this,name,address,phone,email,password,confirmpass));
//                    l1.setOnItemClickListener(myprofile.this);

                    } catch (Exception e) {
                        Toast.makeText(Shopviewprofile.this, "exp"+e, Toast.LENGTH_SHORT).show();

                        Log.d("=========", e.toString());
                    }


                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(Shopviewprofile.this, "err"+error, Toast.LENGTH_SHORT).show();
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
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shopname = e1.getText().toString();
                    phone = e2.getText().toString();
                    place = e3.getText().toString();
                    post = e4.getText().toString();
                    pincode = e5.getText().toString();
                    email = e6.getText().toString();
//                final String password = e5.getText().toString();
//                final String confirmpassword = e6.getText().toString();
                    if (shopname.equalsIgnoreCase("")) {
                        e1.setError("enter shopname");
                    } else if (phone.length() < 10) {
                        e5.setError("Minimun 10 digits required");
                        e5.requestFocus();

                    } else if (place.equalsIgnoreCase("")) {
                        e2.setError("enter place");
                    } else if (post.equalsIgnoreCase("")) {
                        e3.setError("enter post");
                    } else if (pincode.equalsIgnoreCase("")) {
                        e4.setError("enter pincode");

                    } else if (email.equalsIgnoreCase("")) {
                        e6.setError("enter email");
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        e6.setError("Enter valid email");
                        e6.requestFocus();
                    }
//                else if (password.equalsIgnoreCase("")) {
//                    e5.setError("Enter Your Password");
//                } else if (password.length() != 6) {
//                    e5.setError("Minimum 6 nos required");
//                    e5.requestFocus();
//                } else if (confirmpassword.length() != 6) {
//                    e6.setError("Minimum 6 nos required");
//                    e6.requestFocus();
//                }
                    else {

                        RequestQueue queue = Volley.newRequestQueue(Shopviewprofile.this);
                        url = "http://" + sh.getString("ip", "")+":5000/updateprofile";
                        // Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the response string.
                                Log.d("+++++++++++++++++", response);
                                try {

                                    JSONObject json = new JSONObject(response);
                                    String res = json.getString("task");

                                    if (res.equalsIgnoreCase("Success")) {
                                        Toast.makeText(Shopviewprofile.this, "success", Toast.LENGTH_SHORT).show();

                                        Intent ik = new Intent(getApplicationContext(), ShopHome.class);
                                        startActivity(ik);

                                    } else {

                                        Toast.makeText(Shopviewprofile.this, "fail", Toast.LENGTH_SHORT).show();

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
                                params.put("shopname", shopname);
                                params.put("phone", phone);
                                params.put("place", place);
                                params.put("post", post);
                                params.put("pincode", pincode);
                                params.put("email", email);
                                params.put("lid",sh.getString("lid",""));

//                            params.put("password", password);
//                            params.put("confirmpass", confirmpassword);

                                return params;
                            }
                        };
                        queue.add(stringRequest);
                    }
                }
            });
        }
    }
