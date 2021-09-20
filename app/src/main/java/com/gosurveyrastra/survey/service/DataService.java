package com.gosurveyrastra.survey.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.gosurveyrastra.survey.SessionManager.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

import retrofit.client.Response;

import static com.gosurveyrastra.survey.MainActivity.DATABASE_NAME;
import static com.gosurveyrastra.survey.ui.HomeFragment.formid;
import static com.gosurveyrastra.survey.ui.HomeFragment.lat1;
import static com.gosurveyrastra.survey.ui.HomeFragment.lon1;

public class DataService extends Service {

    public static boolean service_status = false;
    Thread dataThread;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    Context context;
    private String recordFile;
    IBinder mBinder = new LocalBinder();
    public static String finalpathstore;
    String cDate;
    TimerCounter tc;
    private int counter = 0;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 1000;
    SQLiteDatabase mDatabase;
    List<Surverys> employeeList;
    private RestClient restClient;
    int i=0;

    public DataService() {
    }

    public class LocalBinder extends Binder {
        public DataService getServerInstance() {
            return DataService.this;
        }
    }

    final class MyThreadClass implements Runnable {

        int service_id;

        MyThreadClass(int service_id) {
            this.service_id = service_id;
        }

        @Override
        public void run() {
            int i = 0;
            synchronized (this) {
                while (dataThread.getName() == "showNotification") {
                    try {
                        wait(1000);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mTimer = new Timer();
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Log.e("strrrrrrrrrrrr", "Data Service onCreated");
        tc = new TimerCounter();
        employeeList = new ArrayList<>();
        restClient = new RestClient();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!service_status) {
            service_status = true;
            dataThread = new Thread(new MyThreadClass(startId));
            dataThread.setName("showNotification");
            dataThread.start();
        }
        Log.e("strrrrrrrrrrrr", "Data Service onStartCommand");
        tc.startTimer(counter);
        return START_STICKY;
    }

    private class TimerTaskToGetInternetStatus extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    icConnected();
                }
            });
        }
    }


    public void startrecord(){
        Log.e("strrrrrrr","started record");
        if (isRecording) {
            stoprecordings();
            isRecording = false;
        } else {
            startRecording();
            isRecording = true;
        }
    }


    public void stoprecord(){
        Log.e("strrrrrrr","started stoped");
        if (isRecording) {
            stopRecording("a","b");
            isRecording = false;
        } else {
            startRecording();
            isRecording = true;
        }
    }

    public void stoprecordings(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        startRecording();
    }


    public void starttimers(){
        try {
            mTimer.schedule(new TimerTaskToGetInternetStatus(), 5, notify_interval);
        } catch (Exception e) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTaskToGetInternetStatus(), 5, notify_interval);
        }
    }

    public void stopRecording(String reqa,String formname) {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;
        if(reqa.equalsIgnoreCase("a")){
        }else {
            Log.e("strrrrrr", "" + formid);
            Log.e("strrrrrr", "" + reqa);
            Log.e("strrrrrr", "" + finalpathstore);
            Log.e("strrrrrr", "" + lat1);
            Log.e("strrrrrr", "" + lon1);
            Log.e("strrrrrr", "" + formid);
            Log.e("strrrrrr", "" + formname);
            String currentDateandTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Log.e("strrrrrr", "" + currentDateandTime);

            String insertSQL = "INSERT INTO surveryes \n" +
                    "(questionid, question_type_name, question_name" +
                    ", answer,surveyids,salary,formname,instime)\n" +
                    "VALUES \n" +
                    "(?, ?, ?, ?, ?, ?, ?, ?);";
            mDatabase.execSQL(insertSQL, new String[]{"" + formid, reqa, "" + finalpathstore,
                    "" + lat1, "" + lon1, "" + formid,""+formname,""+currentDateandTime});
            employeeList = new ArrayList<>();
            try {
                mTimer.schedule(new TimerTaskToGetInternetStatus(), 5, notify_interval);
            } catch (Exception e) {
                mTimer = new Timer();
                mTimer.schedule(new TimerTaskToGetInternetStatus(), 5, notify_interval);
            }
        }
    }

    private void startRecording() {
        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("MMddhhmmss", Locale.ENGLISH);
        Date now = new Date();
        recordFile = formatter.format(now) + ".mp3";
        finalpathstore=recordPath + "/" + recordFile;
        Log.e("strrrrrrrr",""+finalpathstore);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service_status = false;
        Log.e("strrrrrrrrrrrr", "Data Service onDestroy");
        Intent broadcastIntent = new Intent("com.gosurveyrastra.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        tc.stopTimerTask();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("strrrrrrrrrrrr", "Data Service onTaskRemoved");
        tc.stopTimerTask();
        stopService(new Intent(context,DataService.class));
        startService(new Intent(context,DataService.class));
        long ct = System.currentTimeMillis(); //get current time
        Intent restartService = new Intent(getApplicationContext(),
                DataService.class);
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 0, restartService,
                0);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, ct, 1 * 1000, restartServicePI);
    }
    int i1=0;
    public boolean icConnected()
    {
        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            hitAPI();
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            return false;
        }
        return false;
    }

    public void hitAPI(){
        Cursor cursorEmployees = mDatabase.rawQuery("SELECT * FROM surveryes", null);
        if (cursorEmployees.moveToFirst()) {
            do {
                employeeList.add(new Surverys(
                        cursorEmployees.getInt(0),
                        cursorEmployees.getString(1),
                        cursorEmployees.getString(2),
                        cursorEmployees.getString(3),
                        cursorEmployees.getString(4),
                        cursorEmployees.getString(5),
                        cursorEmployees.getString(6),
                        cursorEmployees.getString(7),
                        cursorEmployees.getString(8)
                ));
            } while (cursorEmployees.moveToNext());
        }
        if(cursorEmployees.getCount()==0){
            i=1;
        }else {
            Log.e("strrrrr","before hitting api");
            mDatabase.execSQL("delete from surveryes");
            for(int j=0;j<employeeList.size();j++){
                Log.e("strrrrr","before hitting api    "+employeeList.get(j).getQuestion_name());

                TypedFile typedFile = new TypedFile("multipart/form-data", new File(employeeList.get(j).getQuestion_name()));
                int finalJ = j;
                int finalJ1 = j;
                Log.e("strrrrrrrrrrrrrrrrrrrrr","str   "+employeeList.get(finalJ).getFormname());
                Log.e("strrrrrrrrrrrrrrrrrrrrr","str   "+employeeList.get(finalJ).getQuestionid());
                restClient.getService().upload(typedFile,""+employeeList.get(finalJ).getFormname(),Integer.parseInt(employeeList.get(finalJ).getQuestionid()), new Callback<retrofit.client.Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                        try {
                            JSONObject obj = new JSONObject(bodyString);
                            Log.e("strrrrrrrrrrrrrr",""+bodyString);
                            Log.e("strrrrrrrrrrrrrr",""+employeeList.get(finalJ).getAnswer());
                            if(employeeList.get(finalJ).getAnswer().length()==0){
                                i = 1;
                            }else {
                                PrefManager prefManager=new PrefManager(context);
                                String userid=prefManager.getuserId();
                                String urls="http://prosurvey.in/API/PollAPI/AddFormPost?FormId="+employeeList.get(finalJ).getQuestionid()+"&AduioUrl="+obj.getString("strfilenames")+"&Latitude="+employeeList.get(finalJ).getAnswer()+"&Longitude="+employeeList.get(finalJ).getSurveyids()+"&UserId="+userid+"&RegistrationDate="+employeeList.get(finalJ).getCurrentDateandTime();
                                Log.e("strrrrrrr",""+urls);
                                JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, urls,new JSONArray(employeeList.get(finalJ).getQuestion_type_name()),
                                        new com.android.volley.Response.Listener<JSONArray>() {
                                            @Override
                                            public void onResponse(JSONArray response) {

                                            }
                                        }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
//                                            employeeList.clear();
                                        Log.e("strrrrrrrrrrrrrr",""+error);
//                                            i = 1;
//                                            mDatabase.execSQL("delete from surveryes");
                                        Log.e("strrrrrrrr", "Hit API Hit API ");
                                    }
                                }){
                                    @Override
                                    public Map<String,String> getHeaders() throws AuthFailureError {
                                        Map<String,String> headers = new HashMap<String, String>();
                                        headers.put("Content-Type", "application/json; charset=utf-8");
                                        return headers;
                                    } };
                                RequestQueue requestQueue2 = Volley.newRequestQueue(context);
                                req.setRetryPolicy(new DefaultRetryPolicy(
                                        200000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                requestQueue2.add(req);

                                try {
                                    SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
                                    int surverytaken = prefs.getInt("surverytaken", 0);
                                    Log.e("strrrrrr tak,",""+surverytaken);
                                    surverytaken=0;
                                    Log.e("strrrrrr tak,",""+surverytaken);
                                    SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                                    editor.putInt("surverytaken",surverytaken);
                                    editor.commit();
                                    Log.e("strrrrrrrrrrrrrrs",""+response);
                                }catch (Exception e){
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("strrrrrrr",""+error.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}


