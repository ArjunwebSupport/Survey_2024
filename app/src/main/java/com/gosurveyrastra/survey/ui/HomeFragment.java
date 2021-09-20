package com.gosurveyrastra.survey.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gosurveyrastra.survey.R;
import com.gosurveyrastra.survey.SessionManager.PrefManager;
import com.gosurveyrastra.survey.SurveyDetailsActivity;
import com.gosurveyrastra.survey.service.DataService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    DataService mServer;
    boolean mBounded;
    private static String recordPermission = Manifest.permission.RECORD_AUDIO;
    private static int PERMISSION_CODE = 21; // random
    FusedLocationProviderClient client;
    public static double lat, lon;
    public static String lat1 = "address", lon1 = "address";
    public static String lat2 = "address", lon2 = "address";
    public static String centerLatitude = "address", centerLongitude = "address";
    public static float testLatitude = 0.0f, testLongitude = 0.0f;
    public static float resultlat = 0.0f, resultlong = 0.0f;
    SharedPreferences.Editor editor;
    public static ProgressDialog mProgressDialog;
    RecyclerView recyclerView;
    ArrayList<SurveyListModel> dataModelArrayList;
    SurveyListAdapter chatListAdapter;
    TextView noevents;
    public static String formid;
    String surveylist;
    String formname;
    String formids;
    String userid;
    String email;
    String firstname;
    String pwd;
    int surverytaken;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        PrefManager prefManager = new PrefManager(getActivity());
        userid = prefManager.getuserId();
        email = prefManager.getEmail();
        firstname = prefManager.getFirstName();
        pwd = prefManager.getPwd();
        editor = getActivity().getSharedPreferences("MY_LOCATION", MODE_PRIVATE).edit();
        if (!DataService.service_status) {
            Intent intent = new Intent(getContext(), DataService.class);
            getActivity().startService(intent);
        }
        checkPermissions();
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        recyclerView = root.findViewById(R.id.recycler);
        noevents = root.findViewById(R.id.noevents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        SharedPreferences prefs = getContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        surveylist = prefs.getString("surveylist", "");
        surverytaken = prefs.getInt("surverytaken", 0);
        Log.e("strrrrrr tak,", "" + surverytaken);

        Log.e("strrrrrr", "" + surveylist);
        ConnectivityManager connec =
                (ConnectivityManager) getActivity().getSystemService(getActivity().getBaseContext().CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            fetchingJSON();
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            try {
                JSONObject obj = new JSONObject(surveylist);
                dataModelArrayList = new ArrayList<>();
                JSONArray dataArray = obj.getJSONArray("FormsList");
                if (dataArray.length() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noevents.setVisibility(View.GONE);
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
                        dataModelArrayList.add(playerModel);
                    }
                    removeSimpleProgressDialog();
                    setupRecycler1();
                } else {
                    removeSimpleProgressDialog();
                    noevents.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
            }
        } else {
            fetchingJSON();
        }
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FormActivity.class));
            }
        });
        String url = "http://prosurvey.in/API/AccountAPI/LogOn?UserName=" + firstname + "&pass=" + pwd;
        url = url.replace(" ", "%20");
        Log.e("strrrrrrr", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("strrrrr", ">>" + response);
                        removeSimpleProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.optString("msg").equalsIgnoreCase("Success")) {
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.user_restriction_dialog, null, false);
                                builder.setView(viewInflated);
                                Button closeBtn = viewInflated.findViewById(R.id.closeBtn);
                                builder.setCancelable(false);
                                AlertDialog show = builder.create();
                                closeBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                                        sharedPreferences.edit().clear().commit();
                                        startActivity(new Intent(getContext(), LoginActivity.class));
                                        getActivity().finish();
                                    }
                                });
                                show.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            removeSimpleProgressDialog();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        removeSimpleProgressDialog();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
        logins();
        return root;
    }

    public void logins() {
        String url = "http://prosurvey.in/API/PollAPI/ActiveUsersTodayAPI?UserId=" + userid;
        url = url.replace(" ", "%20");
        Log.e("strrrrrrr", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("strrrrr", ">>" + response);
                        removeSimpleProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            removeSimpleProgressDialog();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        removeSimpleProgressDialog();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void fetchingJSON() {
        showSimpleProgressDialog(getContext(), "Loading...", "Fetching Details", false);
        String jsonURL = "http://prosurvey.in/API/PollAPI/FormsList?Type=Today&UserId=" + userid;
        jsonURL = jsonURL.replace(" ", "%20");
        Log.e("strrrr", jsonURL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("strrrrr", ">>" + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                            editor.putString("surveylist", response);
                            editor.commit();
                            dataModelArrayList = new ArrayList<>();
                            JSONArray dataArray = obj.getJSONArray("FormsList");
                            if (dataArray.length() > 0) {
                                recyclerView.setVisibility(View.VISIBLE);
                                noevents.setVisibility(View.GONE);
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
                                    dataModelArrayList.add(playerModel);
                                }
                                removeSimpleProgressDialog();
                                setupRecycler1();
                            } else {
                                removeSimpleProgressDialog();
                                noevents.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void setupRecycler1() {
        chatListAdapter = new SurveyListAdapter(getActivity(), dataModelArrayList);
        recyclerView.setAdapter(chatListAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (ActivityCompat.checkSelfPermission(mServer, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mServer, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (ActivityCompat.checkSelfPermission(mServer, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mServer, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            getAddress(lat, lon);
                        } else {
                            Toast.makeText(getContext(), "Please enable location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mServer.startrecord();
                SharedPreferences prefs = getContext().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
                formname = prefs.getString(""+dataModelArrayList.get(position).getFormName(), "");
                formids = prefs.getString(""+dataModelArrayList.get(position).getFormId(), "");
                Log.e("strrrrrrss",""+formname);
                Log.e("strrrrrrss",""+formids);
                ConnectivityManager connec =(ConnectivityManager)getActivity().getSystemService(getActivity().getBaseContext().CONNECTIVITY_SERVICE);
                if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                        connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
                    showSimpleProgressDialog(getContext(), "Loading...","Fetching Details",false);
                    String jsonURL ="http://prosurvey.in/API/PollAPI/FormAPI?FormName="+dataModelArrayList.get(position).getFormName();
                    jsonURL = jsonURL.replace(" ", "%20");
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    formid=""+dataModelArrayList.get(position).getFormId();
                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                                    editor.putString(""+dataModelArrayList.get(position).getFormName(),response);
                                    editor.putString(""+dataModelArrayList.get(position).getFormId(),formid);
                                    editor.commit();
                                    Intent questions = new Intent(getActivity(), SurveyDetailsActivity.class);
                                    questions.putExtra("formanews",""+dataModelArrayList.get(position).getFormName());
                                    questions.putExtra("formid",""+dataModelArrayList.get(position).getFormId());
                                    startActivity(questions);
                                    getActivity().finish();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            20000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(stringRequest);
                } else if (
                        connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
                    if(formname.isEmpty()) {
                        showSimpleProgressDialog(getContext(), "Loading...","Fetching Details",false);
                        String jsonURL ="http://prosurvey.in/API/PollAPI/FormAPI?FormName="+dataModelArrayList.get(position).getFormName();
                        jsonURL = jsonURL.replace(" ", "%20");
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonURL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        formid=""+dataModelArrayList.get(position).getFormId();
                                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                                        editor.putString(""+dataModelArrayList.get(position).getFormName(),response);
                                        editor.putString(""+dataModelArrayList.get(position).getFormId(),formid);
                                        editor.commit();
                                        Intent questions = new Intent(getActivity(), SurveyDetailsActivity.class);
                                        questions.putExtra("formanews",""+dataModelArrayList.get(position).getFormName());
                                        questions.putExtra("formid",""+dataModelArrayList.get(position).getFormId());
                                        startActivity(questions);
                                        getActivity().finish();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                });
                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                20000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue.add(stringRequest);
                    }else {
                        formid=""+formids;
                        Intent questions = new Intent(getActivity(), SurveyDetailsActivity.class);
                        questions.putExtra("formanews",""+dataModelArrayList.get(position).getFormName());
                        questions.putExtra("formid",""+dataModelArrayList.get(position).getFormId());
                        startActivity(questions);
                        getActivity().finish();
                    }

                }else {
                    if(formname.isEmpty()) {
                        showSimpleProgressDialog(getContext(), "Loading...","Fetching Details",false);
                        String jsonURL ="http://prosurvey.in/API/PollAPI/FormAPI?FormName="+dataModelArrayList.get(position).getFormName();
                        jsonURL = jsonURL.replace(" ", "%20");
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonURL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        formid=""+dataModelArrayList.get(position).getFormId();
                                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                                        editor.putString(""+dataModelArrayList.get(position).getFormName(),response);
                                        editor.putString(""+dataModelArrayList.get(position).getFormId(),formid);
                                        editor.commit();
                                        Intent questions = new Intent(getActivity(), SurveyDetailsActivity.class);
                                        questions.putExtra("formanews",""+dataModelArrayList.get(position).getFormName());
                                        questions.putExtra("formid",""+dataModelArrayList.get(position).getFormId());
                                        startActivity(questions);
                                        getActivity().finish();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                });
                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                20000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue.add(stringRequest);
                    }else {
                        formid=""+formids;
                        Intent questions = new Intent(getActivity(), SurveyDetailsActivity.class);
                        questions.putExtra("formanews",""+dataModelArrayList.get(position).getFormName());
                        questions.putExtra("formid",""+dataModelArrayList.get(position).getFormId());
                        startActivity(questions);
                        getActivity().finish();
                    }
                }
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
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

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size()>0){


            Address obj = addresses.get(0);
            String countryName = obj.getCountryName();
            String zipCode = obj.getPostalCode();
            String stateName = obj.getAdminArea();
            String cityName = obj.getLocality();
            String addressLine = obj.getAddressLine(0);
            lat1=""+addressLine+" "+cityName+" "+stateName+" "+zipCode+" ";
            lon1=""+lat+" , "+lng;
            testLatitude= (float) lat;
            testLongitude= (float) lng;

            Log.e("strrrrracenterLatitudea", "" + String.format("%.4f",testLongitude));
            Log.e("strrrrracenterLatitudea", "" + String.format("%.4f",testLatitude));
            Log.e("strrrrracenterLatitudea", "" + String.format("%.4f",resultlat));
            Log.e("strrrrracenterLatitudea", "" + String.format("%.4f",resultlong));

            if(centerLatitude.equalsIgnoreCase("address")){

            }else {
                Log.e("strrrrracenterLatitude", "" + String.format("%.4f", Double.parseDouble(centerLatitude)));
                Log.e("strrrrralon1", "" + String.format("%.4f", Double.parseDouble(lon1)));
            }
            }else {

//                Toast.makeText(mServer, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE );
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        removeSimpleProgressDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent mIntent = new Intent(getContext(), DataService.class);
        getActivity().bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    };

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
        }
    };
}
