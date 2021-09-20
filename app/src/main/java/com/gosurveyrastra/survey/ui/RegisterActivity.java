package com.gosurveyrastra.survey.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gosurveyrastra.survey.MainActivity;
import com.gosurveyrastra.survey.R;
import com.gosurveyrastra.survey.SessionManager.PrefManager;

public class RegisterActivity extends AppCompatActivity {

    EditText username, email, password, name, phonenumber;
    Button btnRegister, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        phonenumber = findViewById(R.id.phonenumber);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        username.setError(null);
        password.setError(null);
        email.setError(null);
        name.setError(null);
        phonenumber.setError(null);
        final String usernames = username.getText().toString();
        final String passwords = password.getText().toString();
        final String emails = email.getText().toString();
        final String names = name.getText().toString();
        final String phonenumbers = phonenumber.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(usernames)) {
            username.setError("Username cant be empty");
            focusView = username;
            cancel = true;
        }
        if (TextUtils.isEmpty(passwords)) {
            password.setError("Password cant be empty");
            focusView = password;
            cancel = true;
        }
        if (TextUtils.isEmpty(emails)) {
            email.setError("Email cant be empty");
            focusView = email;
            cancel = true;
        }
        if (TextUtils.isEmpty(names)) {
            name.setError("Name cant be empty");
            focusView = name;
            cancel = true;
        }
        if (TextUtils.isEmpty(phonenumbers)) {
            phonenumber.setError("Phone numbers cant be empty");
            focusView = phonenumber;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
//            saveLoginDetails(emails, passwords, Integer.toString(0));
            startHomeActivity();
        }
    }
    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void saveLoginDetails(String email, String password, String userid) {
//        new PrefManager(this).saveLoginDetails(email, password, userid);
    }


    private void startHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
