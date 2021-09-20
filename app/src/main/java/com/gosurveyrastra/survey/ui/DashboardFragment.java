package com.gosurveyrastra.survey.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gosurveyrastra.survey.R;
import com.gosurveyrastra.survey.SessionManager.PrefManager;
import com.gosurveyrastra.survey.SurveyDetailsActivity;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class DashboardFragment extends Fragment implements DatePickerDialog.OnDateSetListener {


    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    String userid;
    public static ProgressDialog mProgressDialog;
    ArrayList<SurveyListModel> dataModelArrayList;
    ArrayList<SurveyListModel> dataModelArrayList1;
    DashboardAdapter chatListAdapter;
    SurveyListAdapter chatListAdapter1;

    RecyclerView recyclerView, recyclerView1;
    TextView noevents,noevents1;
    TextView pendinguploads;
    int surverytaken;
    ImageView calenders;
    CardView searchcv;
    EditText fromdate,todate;
    Button search;
    String selectedfromdate="null",selectedtodate="null";
    int i=0;
    int i1=0;
    String date,date111;
    SimpleDateFormat simpleDateFormat;
    LinearLayout linearLayoutData;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        PrefManager prefManager=new PrefManager(getActivity());
        userid=prefManager.getuserId();
        recyclerView = root.findViewById(R.id.recycler);
        recyclerView1 = root.findViewById(R.id.recycler1);
        noevents = root.findViewById(R.id.noevents);
        noevents1 = root.findViewById(R.id.noevents);
        pendinguploads = root.findViewById(R.id.pendinguploads);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView1.setNestedScrollingEnabled(false);
        calenders=root.findViewById(R.id.calenders);
        searchcv=root.findViewById(R.id.searchcv);
        fromdate=root.findViewById(R.id.fromdate);
        todate=root.findViewById(R.id.todate);
        search=root.findViewById(R.id.search);
        linearLayoutData=root.findViewById(R.id.linearLayoutData);
        simpleDateFormat = new SimpleDateFormat("dd MM yyyy", Locale.US);

        SharedPreferences prefs = getContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        surverytaken = prefs.getInt("surverytaken", 0);
        pendinguploads.setText(""+surverytaken);
        Calendar c = Calendar.getInstance();
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedfromdate.equals("null")){
                    fromdate.setError("Select from date");
                }
                if(selectedtodate.equals("null")){
                    todate.setError("Select to date");
                }else {
                    searchcv.setVisibility(View.GONE);
                    fromdate.setError(null);//removes error
                    fromdate.clearFocus();
                    todate.setError(null);//removes error
                    todate.clearFocus();
                    searchdate();
                }
            }
        });
        fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=1;
                showDate(year, month, day, R.style.DatePickerSpinner);
            }
        });

        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i1=1;
                showDate(year, month, day, R.style.DatePickerSpinner);
            }
        });

//        String jsonURL ="http://prosurvey.in/API/PollAPI/SurveyUsersListAPI?UserId="+userid;
//        jsonURL = jsonURL.replace(" ", "%20");
//        Log.e("strrrr", jsonURL);
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonURL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("strrrrr", ">>" + response);
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            dataModelArrayList = new ArrayList<>();
//                            JSONArray dataArray  = obj.getJSONArray("SurveyUsersList");
//                            if(dataArray.length() > 0) {
//                                recyclerView.setVisibility(View.VISIBLE);
//                                noevents.setVisibility(View.GONE);
//                                for (int i = 0; i < dataArray.length(); i++) {
//                                    SurveyListModel playerModel = new SurveyListModel();
//                                    JSONObject dataobj = dataArray.getJSONObject(i);
//                                    playerModel.setFormId(dataobj.getString("FormId"));
//                                    playerModel.setFormName(dataobj.getString("FormName"));
//                                    playerModel.setUserId(dataobj.getString("UserId"));
//                                    playerModel.setNoofRegistrations(dataobj.getString("NoofRegistrations"));
//                                    dataModelArrayList.add(playerModel);
//                                }
//                                setupRecycler1();
//                            }else {
//                                removeSimpleProgressDialog();
//                                noevents.setVisibility(View.VISIBLE);
//                                recyclerView.setVisibility(View.GONE);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
////                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                20000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(stringRequest);

        fetchingJSON();

        calenders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
                surverytaken = prefs.getInt("surverytaken", 0);
                pendinguploads.setText(""+surverytaken);
            }
        });

        return root;
    }
    private void fetchingJSON() {
        showSimpleProgressDialog(getContext(), "Loading...", "Fetching Details", false);
        String jsonURL = "http://prosurvey.in/API/PollAPI/FormsList?UserId=" + userid;
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
                            JSONArray dataArray = obj.getJSONArray("FormsList");
                            if (dataArray.length() > 0) {
                                recyclerView1.setVisibility(View.VISIBLE);
                                noevents1.setVisibility(View.GONE);
                                for (int i = 0; i < dataArray.length(); i++) {
                                    SurveyListModel playerModel = new SurveyListModel();
                                    JSONObject dataobj = dataArray.getJSONObject(i);
                                    playerModel.setFormId(dataobj.getString("FormId"));
                                    playerModel.setFormName(dataobj.getString("FormName"));
                                    playerModel.setStartDate(dataobj.getString("StartDate"));
                                    playerModel.setEndDate(dataobj.getString("EndDate"));
                                    playerModel.setBannerUrl(dataobj.getString("BannerUrl"));
                                    playerModel.setContactEmail(dataobj.getString("ContactEmail"));
                                    playerModel.setAddress(dataobj.getString("Address"));
                                    playerModel.setCity(dataobj.getString("City"));
                                    playerModel.setStateName(dataobj.getString("StateName"));
                                    playerModel.setCountryName(dataobj.getString("CountryName"));
                                    playerModel.setIsFormRegistration(dataobj.getString("IsFormRegistration"));
                                    playerModel.setIsActive(dataobj.getString("IsActive"));
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
                        removeSimpleProgressDialog();

//                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void setupRecycler2() {
        chatListAdapter1 = new SurveyListAdapter(getActivity(), dataModelArrayList1);
        recyclerView1.setAdapter(chatListAdapter1);
        recyclerView1.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView1, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent questions = new Intent(getActivity(), DashboardUsersListActivity.class);
                questions.putExtra("formanews", "" + dataModelArrayList1.get(position).getFormName());
                questions.putExtra("formid", "" + dataModelArrayList1.get(position).getFormId());
                startActivity(questions);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    public void searchdate(){


        String jsonURL ="http://prosurvey.in/API/PollAPI/SurveyDashoardList?StartDate="+selectedfromdate+"&EndDate="+selectedtodate+"&UserId="+userid;
        jsonURL = jsonURL.replace(" ", "%20");
        Log.e("strrrr", jsonURL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("strrrrr", ">>" + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            dataModelArrayList = new ArrayList<>();
                            JSONArray dataArray  = obj.getJSONArray("SurveyDashoardList");
                            if(dataArray.length() > 0) {
                                linearLayoutData.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                noevents.setVisibility(View.GONE);
                                for (int i = 0; i < dataArray.length(); i++) {
                                    SurveyListModel playerModel = new SurveyListModel();
                                    JSONObject dataobj = dataArray.getJSONObject(i);
                                    playerModel.setFormId(dataobj.getString("FormId"));
                                    playerModel.setFormName(dataobj.getString("FormName"));
                                    playerModel.setNoofRegistrations(dataobj.getString("NoofRegistrations"));
                                    dataModelArrayList.add(playerModel);
                                }
                                setupRecycler1();
                            }else {
                                removeSimpleProgressDialog();

                                linearLayoutData.setVisibility(View.VISIBLE);
                                noevents.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
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
                        removeSimpleProgressDialog();

//                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @VisibleForTesting
    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(getContext())
                .callback(DashboardFragment.this)
                .spinnerTheme(spinnerTheme)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();

    }

    @Override
    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        date=simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd MM yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy" );
        SimpleDateFormat targetFormat1 = new SimpleDateFormat("dd/MM/yyyy" );
        Date date1;
        Date date11;
        try {
            date1 = originalFormat.parse(date);
            date11 = originalFormat.parse(date);
//            System.out.println("Old Format :   " + originalFormat.format(date));
            System.out.println("New Format :   " + targetFormat.format(date1));
            date=targetFormat.format(date1);
            date111=targetFormat1.format(date11);
        } catch (ParseException ex) {
            // Handle Exception.
        }
        if(i==1){
            fromdate.setText(date111);
            selectedfromdate=date111;
            i=0;
        }
        if(i1==1){
            todate.setText(date111);
            selectedtodate=date111;
            i1=0;
        }
    }


    private void setupRecycler1(){
        chatListAdapter = new DashboardAdapter(getActivity(),dataModelArrayList);
        recyclerView.setAdapter(chatListAdapter);

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

