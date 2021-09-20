package com.gosurveyrastra.survey.SessionManager;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    Context context;

    public PrefManager(Context context) {
        this.context = context;
    }

    public void saveLoginDetails(String userid, String email, String firstname,String mobileno,String pwd) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userid);
        editor.putString("Email", email);
        editor.putString("firstname", firstname);
        editor.putString("mobileno", mobileno);
        editor.putString("pwd", pwd);
        editor.commit();
    }

    public String getPwd() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("pwd", "");
    }
    public String getMobileNo() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("mobileno", "");
    }
    public String getFirstName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("firstname", "");
    }

    public String getEmail() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }

    public String getuserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }

    public boolean isUserLogedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isEmailEmpty = sharedPreferences.getString("Email", "").isEmpty();
        boolean isPasswordEmpty = sharedPreferences.getString("Password", "").isEmpty();
        return isEmailEmpty || isPasswordEmpty;
    }




    public void storeAllDetals(int userid,String mobilenumber,String usenrmae,String emailid){
       SharedPreferences sharedPreferences=context.getSharedPreferences("storingdata",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("userid",userid);
        editor.putString("mobilenumber",""+mobilenumber);
        editor.putString("username",""+usenrmae);
        editor.putString("emailid",""+emailid);
        editor.commit();
    }

    public void retriveuserdetails(String userid){
        SharedPreferences sharedPreferences=context.getSharedPreferences("storingdata",Context.MODE_PRIVATE);
        int userid1=sharedPreferences.getInt("userid",0);
        String username=sharedPreferences.getString("username","null");
        String emailid=sharedPreferences.getString("emailid","null");
        String mobilenumber=sharedPreferences.getString("mobielnumber","");
    }
}