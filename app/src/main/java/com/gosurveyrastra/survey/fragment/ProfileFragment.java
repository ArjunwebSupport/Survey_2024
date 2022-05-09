package com.gosurveyrastra.survey.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.gosurveyrastra.survey.R;
import com.gosurveyrastra.survey.ui.LoginActivity;

public class ProfileFragment extends Fragment {


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String Email=sharedPreferences.getString("Email","");
        String firstname=sharedPreferences.getString("firstname","");
        String mobileno=sharedPreferences.getString("mobileno","");
        TextView phonnumber=root.findViewById(R.id.phonnumber);
        TextView emailid=root.findViewById(R.id.emailid);
        TextView address=root.findViewById(R.id.address);
        if(mobileno.equalsIgnoreCase("null")){
            phonnumber.setText("--");
        }else {
            phonnumber.setText("" + mobileno);
        }if(Email.equalsIgnoreCase("null")){
            emailid.setText("--");
        }else {
            emailid.setText(""+Email);
        }if(firstname.equalsIgnoreCase("null")){
            address.setText("--");
        }else {
            address.setText(""+firstname);
        }
        Button logout=root.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().clear().commit();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
        return root;
    }
}

