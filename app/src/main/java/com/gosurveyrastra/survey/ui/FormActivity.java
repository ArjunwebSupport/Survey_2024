package com.gosurveyrastra.survey.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gosurveyrastra.survey.R;
import com.gosurveyrastra.survey.SessionManager.PrefManager;
import com.gosurveyrastra.survey.feedbackform.FormThankYou;

import org.json.JSONException;
import org.json.JSONObject;

public class FormActivity extends AppCompatActivity {

    EditText etname,etemail,etmobile,etaddress;
    public static ProgressDialog mProgressDialog;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        PrefManager prefManager=new PrefManager(this);
        userid=prefManager.getuserId();
        ImageView navagitionvie=findViewById(R.id.navagitionvie);
        navagitionvie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etname=findViewById(R.id.etname);
        etemail=findViewById(R.id.etemail);
        etmobile=findViewById(R.id.etmobile);
        etaddress=findViewById(R.id.etaddress);
        Button tq_homebtn=findViewById(R.id.tq_homebtn);
        tq_homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etname.getText().toString())){
                    Toast.makeText(FormActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                    etname.setFocusable(true);
                }else if(TextUtils.isEmpty(etemail.getText().toString())){
                    Toast.makeText(FormActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    etemail.setFocusable(true);
                }else if(TextUtils.isEmpty(etmobile.getText().toString())){
                    Toast.makeText(FormActivity.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
                    etmobile.setFocusable(true);
                }else if(TextUtils.isEmpty(etaddress.getText().toString())){
                    Toast.makeText(FormActivity.this, "Enter Address", Toast.LENGTH_SHORT).show();
                    etaddress.setFocusable(true);
                }else {
                    showSimpleProgressDialog(FormActivity.this, "Loading...","Fetching Details",false);
                    String jsonURL ="http://prosurvey.in/API/PollAPI/AddUsersData?UserId="+ userid+"&Name="+etname.getText().toString()+"&Email="+etemail.getText().toString()+"&PhoneNo="+etmobile.getText().toString()+"&Adress="+etaddress.getText().toString();
                    jsonURL = jsonURL.replace(" ", "%20");
                    Log.e("strrrrrr",""+jsonURL);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    removeSimpleProgressDialog();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (obj.optString("msg").equalsIgnoreCase("Success")) {
                                            startActivity(new Intent(FormActivity.this, FormThankYou.class));
                                            finish();
                                        }else {
                                            Toast.makeText(FormActivity.this, ""+obj.optString("msg"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        removeSimpleProgressDialog();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                    RequestQueue requestQueue = Volley.newRequestQueue(FormActivity.this);
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            20000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(stringRequest);
                }
            }
        });
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}