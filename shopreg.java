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
import android.widget.TextView;
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

public class shopreg extends AppCompatActivity {
    EditText e1,e2,e3,e4,e5,e6,e7,e8;
    Button b1;
    SharedPreferences sh;
    String ip;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopreg);
        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        e1=(EditText)findViewById(R.id.editTextTextPersonName2);
        e2=(EditText)findViewById(R.id.editTextTextPersonName4);
        e3=(EditText)findViewById(R.id.editTextTextPersonName5);
        e4=(EditText)findViewById(R.id.editTextTextPersonName6);
        e5=(EditText)findViewById(R.id.editTextTextPersonName7);
        e6=(EditText)findViewById(R.id.editTextTextPersonName8);
        e7=(EditText)findViewById(R.id.editTextTextPassword2);
        e8=(EditText)findViewById(R.id.editTextTextPassword3);
        b1=(Button)findViewById(R.id.button3);

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String shopname = e1.getText().toString();
                    final String place = e2.getText().toString();
                    final String post = e3.getText().toString();
                    final String pincode = e4.getText().toString();
                    final String phone = e5.getText().toString();
                    final String email = e6.getText().toString();
                    final String password = e7.getText().toString();
                    final String confirmpassword = e8.getText().toString();
                    if (shopname.equalsIgnoreCase("")) {
                        e1.setError("Enter Your Name");
                    } else if (!shopname.matches("^[a-zA-Z]*$")) {
                        e1.setError("characters allowed");
                    }else if (place.equalsIgnoreCase("")) {

                        e2.setError("Enter Your Place");
                    }
                    else if (pincode.equalsIgnoreCase("")) {
                        e4.setError("Enter Your Pin");

                    } else if (pincode.length() != 6) {
                        e6.setError("invalid pin");
                        e6.requestFocus();


                    } else if(place.equalsIgnoreCase(""))
                    {
                        e3.setError("Enter Your Place");
                    }


                    else if (phone.equalsIgnoreCase("")) {
                        e5.setError("Enter Your Phone No");
                    } else if (phone.length() < 10) {
                        e5.setError("Minimum 10 nos required");
                        e5.requestFocus();


                    } else if (email.equalsIgnoreCase("")) {
                        e6.setError("Enter Your Email");
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        e6.setError("Enter Valid Email");
                        e6.requestFocus();
                    }   else if(password.equalsIgnoreCase("")) {
                        e7.setError("Enter Your Password");
                    }
                    else if(password.length()!=6)
                        {
                            e7.setError(" 6 nos required");
                            e7.requestFocus();
                        }   else if(confirmpassword.equalsIgnoreCase("")) {
                        e8.setError("Enter Your confirm Password");
                    }
                    else if(confirmpassword.length()!=6)
                    {
                        e8.setError(" 6 nos required");
                        e8.requestFocus();
                    } else

                {
                    RequestQueue queue = Volley.newRequestQueue(shopreg.this);
                    url = "http://" + sh.getString("ip", "") + ":5000/shop_reg";

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
                                    Toast.makeText(shopreg.this, "Registration success", Toast.LENGTH_SHORT).show();

                                    Intent ik = new Intent(getApplicationContext(), SLOGIN.class);
                                    startActivity(ik);

                                } else {

                                    Toast.makeText(shopreg.this, "Invalid username or password", Toast.LENGTH_SHORT).show();

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
                            params.put("place", place);
                            params.put("post", post);
                            params.put("pin", pincode);
                            params.put("phone", phone);
                            params.put("uname", email);
                            params.put("password", password);
                            params.put("confirm password", confirmpassword);


                            return params;
                        }
                    };
                    queue.add(stringRequest);

                    }

                }
            });



    }
}