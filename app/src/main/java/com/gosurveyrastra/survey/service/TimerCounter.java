package com.gosurveyrastra.survey.service;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class TimerCounter {
    int counter;

    public void Timer() {
    }

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer(int counter) {
        this.counter = counter;
         timer = new Timer();
         initializeTimerTask();
         timer.schedule(timerTask, 1000, 1000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
            }
        };
    }

    public void stopTimerTask() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
