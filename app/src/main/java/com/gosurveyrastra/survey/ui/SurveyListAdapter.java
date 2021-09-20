package com.gosurveyrastra.survey.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.gosurveyrastra.survey.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SurveyListAdapter  extends RecyclerView.Adapter<SurveyListAdapter.MyViewHolder> {

    Context context;
    private ArrayList<SurveyListModel> dataModelArrayList;
    private LayoutInflater inflater;

    public SurveyListAdapter(Context ctx, ArrayList<SurveyListModel> dataModelArrayList ){
        context=ctx;
        inflater = LayoutInflater.from(ctx);
        this.dataModelArrayList = dataModelArrayList;
    }
    @Override
    public SurveyListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.surveyadapter, parent, false);
        SurveyListAdapter.MyViewHolder holder = new SurveyListAdapter.MyViewHolder(view);
        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(SurveyListAdapter.MyViewHolder holder, final int position) {
        if(TextUtils.isEmpty(dataModelArrayList.get(position).getBannerUrl())){
            holder.icon.setImageResource(R.drawable.home_icon3);
        }else {
            Picasso.get().load(dataModelArrayList.get(position).getBannerUrl()).error(R.drawable.home_icon3).into(holder.icon);
        }
        holder.titles.setText(dataModelArrayList.get(position).getFormName());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        LinearLayout feedback;
        ImageView icon;
        TextView titles;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            feedback = itemView.findViewById(R.id.feedback);
            icon = itemView.findViewById(R.id.icon);
            titles = itemView.findViewById(R.id.titles);
        }

        @Override
        public void onClick(View v) {
        }

    }
}

