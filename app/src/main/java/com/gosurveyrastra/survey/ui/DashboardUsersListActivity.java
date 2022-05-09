package com.gosurveyrastra.survey.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gosurveyrastra.survey.R;
import com.gosurveyrastra.survey.SessionManager.PrefManager;
import com.gosurveyrastra.survey.SurveyDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardUsersListActivity extends AppCompatActivity {

    ArrayList<SurveyListModel> dataModelArrayList;

    public static ProgressDialog mProgressDialog;
    DashboardUsersListAdapter chatListAdapter1;
    String userid;
    ArrayList<DashboardUsersModel> dataModelArrayList1;

    RecyclerView recyclerView1;
    TextView noevents1;
    String formId, formName;
    TextView tvformName ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_users_list);
        formId = getIntent().getExtras().getString("formid");
        formName = getIntent().getExtras().getString("formanews");
        PrefManager prefManager=new PrefManager(this);
        userid=prefManager.getuserId();
        recyclerView1 = findViewById(R.id.recycler1);
        noevents1 = findViewById(R.id.noevents1);
        tvformName = findViewById(R.id.formName);
        recyclerView1.setLayoutManager(new LinearLayoutManager(DashboardUsersListActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView1.setNestedScrollingEnabled(false);
        ImageView navagitionvie=findViewById(R.id.navagitionvie);
        navagitionvie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fetchingJSON();
        tvformName.setText(""+formName);
        TextView emailid=findViewById(R.id.emailid);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String Email=sharedPreferences.getString("Email","");
        String firstname=sharedPreferences.getString("firstname","");
        String mobileno=sharedPreferences.getString("mobileno","");
        if(Email.equalsIgnoreCase("null")){
            emailid.setText("--");
            if(firstname.equalsIgnoreCase("null")){
                emailid.setText("--");
            }else {
                emailid.setText(""+firstname);
            }
        }else {
            emailid.setText(""+Email);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void fetchingJSON() {
        showSimpleProgressDialog(DashboardUsersListActivity.this, "Loading...", "Fetching Details", false);
        String jsonURL = "https://prosurvey.in/API/PollAPI/DailyReportDashoardList?UserId=" + userid+"&FormId="+formId;
        jsonURL = jsonURL.replace(" ", "%20");
        Log.e("strrrr", jsonURL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("strrrrr", ">>" + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            dataModelArrayList1 = new ArrayList<>();
                            JSONArray dataArray = obj.getJSONArray("FormRegistersList");
                            if (dataArray.length() > 0) {
                                recyclerView1.setVisibility(View.VISIBLE);
                                noevents1.setVisibility(View.GONE);
                                for (int i = 0; i < dataArray.length(); i++) {
                                    DashboardUsersModel playerModel = new DashboardUsersModel();
                                    JSONObject dataobj = dataArray.getJSONObject(i);
                                    playerModel.setLastName(dataobj.getString("LastName"));
                                    playerModel.setDate(dataobj.getString("Date"));
                                    playerModel.setUserId(dataobj.getInt("UserId"));
                                    playerModel.setNoofUserCount(dataobj.getInt("NoofUserCount"));
                                    dataModelArrayList1.add(playerModel);
                                }
                                removeSimpleProgressDialog();
                                setupRecycler2();
                            } else {
                                removeSimpleProgressDialog();
                                noevents1.setVisibility(View.VISIBLE);
                                recyclerView1.setVisibility(View.GONE);
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
//                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        removeSimpleProgressDialog();

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(DashboardUsersListActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void setupRecycler2() {
        chatListAdapter1 = new DashboardUsersListAdapter(DashboardUsersListActivity.this, dataModelArrayList1);
        recyclerView1.setAdapter(chatListAdapter1);

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