package com.gosurveyrastra.survey.feedbackform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gosurveyrastra.survey.MainActivity;
import com.gosurveyrastra.survey.R;
import com.gosurveyrastra.survey.SurveyDetailsActivity;
import com.gosurveyrastra.survey.service.DataService;

public class ThankYouScreen extends AppCompatActivity {

    DataService mServer;
    boolean mBounded;
    String reqs;
    String formid;
    String formanews;
    String DisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you_screen);
        reqs=getIntent().getStringExtra("reqs");
        formid=getIntent().getStringExtra("formid");
        formanews=getIntent().getStringExtra("formanews");
        DisplayName=getIntent().getStringExtra("DisplayName");
        if (!DataService.service_status) {
            Intent intent = new Intent(ThankYouScreen.this, DataService.class);
            startService(intent);
        }
        ImageView navagitionvie=findViewById(R.id.navagitionvie);
        navagitionvie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThankYouScreen.this, MainActivity.class));
                finish();
            }
        });

        Button tq_homebtn=findViewById(R.id.tq_homebtn);
        tq_homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThankYouScreen.this, MainActivity.class));
                finish();
            }
        });
        Button tq_homebtn1=findViewById(R.id.tq_homebtn1);
        tq_homebtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServer.startrecord();
                Intent questions = new Intent(ThankYouScreen.this, SurveyDetailsActivity.class);
                questions.putExtra("formanews",""+formanews);
                questions.putExtra("formid",""+formid);
                questions.putExtra("DisplayName",""+DisplayName);
                startActivity(questions);
                finish();
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        reqs=getIntent().getStringExtra("reqs");
        try{
            Intent mIntent = new Intent(ThankYouScreen.this, DataService.class);
            bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        }catch (Exception e){

        }
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
            mServer.stopRecording(reqs,formanews);
        }
    };

}