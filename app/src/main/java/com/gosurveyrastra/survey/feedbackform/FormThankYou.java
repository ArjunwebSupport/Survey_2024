package com.gosurveyrastra.survey.feedbackform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.gosurveyrastra.survey.MainActivity;
import com.gosurveyrastra.survey.R;
import com.gosurveyrastra.survey.SurveyDetailsActivity;
import com.gosurveyrastra.survey.service.DataService;
import com.gosurveyrastra.survey.ui.FormActivity;

public class FormThankYou extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_thank_you);
        ImageView navagitionvie=findViewById(R.id.navagitionvie);
        navagitionvie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FormThankYou.this, MainActivity.class));
                finish();
            }
        });

        Button tq_homebtn=findViewById(R.id.tq_homebtn);
        tq_homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FormThankYou.this, MainActivity.class));
                finish();
            }
        });

        Button tq_homebtn1=findViewById(R.id.tq_homebtn1);
        tq_homebtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent questions = new Intent(FormThankYou.this, FormActivity.class);
                startActivity(questions);
                finish();
            }
        });
    }
}