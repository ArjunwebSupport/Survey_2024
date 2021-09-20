package com.gosurveyrastra.survey.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gosurveyrastra.survey.MainActivity;
import com.gosurveyrastra.survey.R;
import com.gosurveyrastra.survey.SessionManager.PrefManager;

public class SplashScreenActivity extends AppCompatActivity {

    Handler handler;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    connected = true;
                    isInternetAvailable();
                }
                else{
                        connected = false;
                    isInternetNotAvailable();
                }
            }
        };
        handler.postDelayed(r, 1000);
    }

   private void isInternetAvailable(){
        if(new PrefManager(this).getEmail().equals("")){
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void isInternetNotAvailable(){
        Toast.makeText(this, "Internet Not Available", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
