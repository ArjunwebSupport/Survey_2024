package com.gosurveyrastra.survey.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import com.gosurveyrastra.survey.feedbackform.ThankYouScreen;
import com.gosurveyrastra.survey.service.DataService;
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
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.MODE_PRIVATE;
import static com.gosurveyrastra.survey.MainActivity.DATABASE_NAME;
import static com.gosurveyrastra.survey.MainActivity.Internetcheck;


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
    DataService mServer;
    boolean mBounded;
    SQLiteDatabase mDatabase;
    private Timer mTimer = null;
    private Handler mHandler = new Handler();
    long notify_interval = 1000;
    TextView tms18,ams18,tfs18,afs18,tts18,tas18;
    TextView tms26,ams26,tfs26,afs26,tts26,tas26;
    TextView tms41,ams41,tfs41,afs41,tts41,tas41;
    TextView tms60,ams60,tfs60,afs60,tts60,tas60,targetsample;

    TextView tmsbc,amsbc,tmsoc,amsoc,tmssc,amssc,tmsst,amsst;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mDatabase = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        PrefManager prefManager=new PrefManager(getActivity());
        userid=prefManager.getuserId();
        recyclerView = root.findViewById(R.id.recycler);


        targetsample = root.findViewById(R.id.targetsample);
        tmsbc = root.findViewById(R.id.tmsbc);
        amsbc = root.findViewById(R.id.amsbc);
        tmsoc = root.findViewById(R.id.tmsoc);
        amsoc = root.findViewById(R.id.amsoc);
        tmssc = root.findViewById(R.id.tmssc);
        amssc = root.findViewById(R.id.amssc);
        tmsst = root.findViewById(R.id.tmsst);
        amsst = root.findViewById(R.id.amsst);
        tms60 = root.findViewById(R.id.tms60);


        ams60 = root.findViewById(R.id.ams60);
        tfs60 = root.findViewById(R.id.tfs60);
        afs60 = root.findViewById(R.id.afs60);
        tts60 = root.findViewById(R.id.tts60);
        tas60 = root.findViewById(R.id.tas60);

        tms41 = root.findViewById(R.id.tms41);
        ams41 = root.findViewById(R.id.ams41);
        tfs41 = root.findViewById(R.id.tfs41);
        afs41 = root.findViewById(R.id.afs41);
        tts41 = root.findViewById(R.id.tts41);
        tas41 = root.findViewById(R.id.tas41);

        tms26 = root.findViewById(R.id.tms26);
        ams26 = root.findViewById(R.id.ams26);
        tfs26 = root.findViewById(R.id.tfs26);
        afs26 = root.findViewById(R.id.afs26);
        tts26 = root.findViewById(R.id.tts26);
        tas26 = root.findViewById(R.id.tas26);

        tms18 = root.findViewById(R.id.tms18);
        ams18 = root.findViewById(R.id.ams18);
        tfs18 = root.findViewById(R.id.tfs18);
        afs18 = root.findViewById(R.id.afs18);
        tts18 = root.findViewById(R.id.tts18);
        tas18 = root.findViewById(R.id.tas18);
        recyclerView1 = root.findViewById(R.id.recycler1);
        noevents = root.findViewById(R.id.noevents);
        noevents1 = root.findViewById(R.id.noevents);
        pendinguploads = root.findViewById(R.id.pendinguploads);
        TextView emailid=root.findViewById(R.id.emailid);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
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
        try{
            String countQuery = "SELECT * FROM surveryes";
            Cursor cursor = mDatabase.rawQuery(countQuery, null);
            int count = cursor.getCount();
            cursor.close();
            Log.e("strrrrrrcheckcount",""+count);
            pendinguploads.setText(""+count);
            if(count==0){
                mTimer = new Timer();
                mTimer.cancel();
            }else {
                mTimer = new Timer();
                mTimer.schedule(new TimerTaskToGetInternetStatus(), 5, notify_interval);
            }
        }catch (Exception e){
        }
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
//        String jsonURL ="https://prosurvey.in/API/PollAPI/SurveyUsersListAPI?UserId="+userid;
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
        if(Internetcheck.equalsIgnoreCase("enable")){
            fetchingJSON();
            fetchingcount();
        }else{

        }
        calenders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Internetcheck.equalsIgnoreCase("enable")){
                    String countQuery = "SELECT * FROM surveryes";
                    Cursor cursor = mDatabase.rawQuery(countQuery, null);
                    int count = cursor.getCount();
                    cursor.close();
                    Log.e("strrrrrrcheckcount", "" + count);
//                SharedPreferences prefs = getContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
//                surverytaken = prefs.getInt("surverytaken", 0);
                    pendinguploads.setText("" + count);
                    if (count > 0) {
                        try {
                            Intent mIntent = new Intent(getContext(), DataService.class);
                            getActivity().bindService(mIntent, mConnection, BIND_AUTO_CREATE);
                        } catch (Exception e) {
                        }
                    }
                }else {
                    Toast.makeText(getContext(), "Internet is Off", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            DataService.LocalBinder mLocalBinder = (DataService.LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();
            mServer.checkcount();
        }
    };


    private class TimerTaskToGetInternetStatus extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    String countQuery = "SELECT * FROM surveryes";
                    Cursor cursor = mDatabase.rawQuery(countQuery, null);
                    int count = cursor.getCount();
                    cursor.close();
                    Log.e("strrrrrrcheckcount",""+count);
                    pendinguploads.setText(""+count);
                    if(count==0){
                        mTimer = new Timer();
                        mTimer.cancel();
                    }else {

                    }
                }
            });
        }
    }

    private void fetchingJSON() {
        showSimpleProgressDialog(getContext(), "Loading...", "Fetching Details", false);
        String jsonURL = "https://prosurvey.in/API/PollAPI/FormsList?UserId=" + userid;
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
                                    playerModel.setDisplayName(dataobj.getString("DisplayName"));

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

    private void fetchingcount() {
        showSimpleProgressDialog(getContext(), "Loading...", "Fetching Details", false);
        String jsonURL = "https://prosurvey.in/API/AccountAPI/TargetCountAPI?UserId=" + userid;
        jsonURL = jsonURL.replace(" ", "%20");
        Log.e("strrrr", jsonURL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("strrrrr", ">>" + response);
                        try {
                            JSONObject   obj = new JSONObject(response);
                            int targetcount=obj.optInt("TargetCount");

                            targetsample.setText("Target Samples - "+targetcount);

                            tms18.setText(""+obj.getString("MaleSample18to25"));
                            tms26.setText(""+obj.getString("MaleSample26to40"));
                            tms41.setText(""+obj.getString("MaleSample41to60"));
                            tms60.setText(""+obj.getString("MaleSampleAbove60"));

                            ams18.setText(""+obj.getString("MaleAgeGroup1"));
                            ams26.setText(""+obj.getString("MaleAgeGroup2"));
                            ams41.setText(""+obj.getString("MaleAgeGroup3"));
                            ams60.setText(""+obj.getString("MaleAgeGroup4"));

                            tfs18.setText(""+obj.getString("FeMaleSample18to25"));
                            tfs26.setText(""+obj.getString("FeMaleSample26to40"));
                            tfs41.setText(""+obj.getString("FeMaleSample41to60"));
                            tfs60.setText(""+obj.getString("FeMaleSampleAbove60"));

                            afs18.setText(""+obj.getString("FemaleAgeGroup1"));
                            afs26.setText(""+obj.getString("FemaleAgeGroup2"));
                            afs41.setText(""+obj.getString("FemaleAgeGroup3"));
                            afs60.setText(""+obj.getString("FemaleAgeGroup4"));

                            tts18.setText(""+obj.getString("TotalTargetSample18to25"));
                            tts26.setText(""+obj.getString("TotalTargetSample26to40"));
                            tts41.setText(""+obj.getString("TotalTargetSample41to60"));
                            tts60.setText(""+obj.getString("TotalTargetSampleAbove60"));

                            tas18.setText(""+obj.getString("TotalActualAgeGroup1"));
                            tas26.setText(""+obj.getString("TotalActualAgeGroup2"));
                            tas41.setText(""+obj.getString("TotalActualAgeGroup3"));
                            tas60.setText(""+obj.getString("TotalActualAgeGroup4"));

                            tmsbc.setText(""+obj.getString("Caste50BC"));
                            tmsoc.setText(""+obj.getString("Caste20OC"));
                            tmssc.setText(""+obj.getString("Caste20SC"));
                            tmsst.setText(""+obj.getString("Caste10ST"));

                            amsbc.setText(""+obj.getString("CasteBC"));
                            amsoc.setText(""+obj.getString("CasteOC"));
                            amssc.setText(""+obj.getString("CasteSC"));
                            amsst.setText(""+obj.getString("CasteST"));

                            removeSimpleProgressDialog();
//                            dataModelArrayList1 = new ArrayList<>();
//                            JSONArray dataArray = obj.getJSONArray("FormsList");
//                            if (dataArray.length() > 0) {
//                                recyclerView1.setVisibility(View.VISIBLE);
//                                noevents1.setVisibility(View.GONE);
//                                for (int i = 0; i < dataArray.length(); i++) {
//                                    SurveyListModel playerModel = new SurveyListModel();
//                                    JSONObject dataobj = dataArray.getJSONObject(i);
//                                    playerModel.setFormId(dataobj.getString("FormId"));
//                                    playerModel.setFormName(dataobj.getString("FormName"));
//                                    playerModel.setStartDate(dataobj.getString("StartDate"));
//                                    playerModel.setEndDate(dataobj.getString("EndDate"));
//                                    playerModel.setBannerUrl(dataobj.getString("BannerUrl"));
//                                    playerModel.setContactEmail(dataobj.getString("ContactEmail"));
//                                    playerModel.setAddress(dataobj.getString("Address"));
//                                    playerModel.setCity(dataobj.getString("City"));
//                                    playerModel.setStateName(dataobj.getString("StateName"));
//                                    playerModel.setCountryName(dataobj.getString("CountryName"));
//                                    playerModel.setIsFormRegistration(dataobj.getString("IsFormRegistration"));
//                                    playerModel.setIsActive(dataobj.getString("IsActive"));
//                                    dataModelArrayList1.add(playerModel);
//                                }
//                                removeSimpleProgressDialog();
//                                setupRecycler2();
//                            } else {
//                                removeSimpleProgressDialog();
//                                noevents1.setVisibility(View.VISIBLE);
//                                recyclerView1.setVisibility(View.GONE);
//                            }
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


        String jsonURL ="https://prosurvey.in/API/PollAPI/SurveyDashoardList?StartDate="+selectedfromdate+"&EndDate="+selectedtodate+"&UserId="+userid;
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

