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

public class DashboardUsersListAdapter extends RecyclerView.Adapter<DashboardUsersListAdapter.MyViewHolder> {

    Context context;
    private ArrayList<DashboardUsersModel> dataModelArrayList;
    private LayoutInflater inflater;

    public DashboardUsersListAdapter(Context ctx, ArrayList<DashboardUsersModel> dataModelArrayList ){
        context=ctx;
        inflater = LayoutInflater.from(ctx);
        this.dataModelArrayList = dataModelArrayList;
    }
    @Override
    public DashboardUsersListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_user_item, parent, false);
        DashboardUsersListAdapter.MyViewHolder holder = new DashboardUsersListAdapter.MyViewHolder(view);
        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(DashboardUsersListAdapter.MyViewHolder holder, final int position) {

        holder.tvDate.setText(dataModelArrayList.get(position).getDate());
        holder.tvNoOfusers.setText(""+dataModelArrayList.get(position).getNoofUserCount());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvDate, tvNoOfusers;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNoOfusers = itemView.findViewById(R.id.tvNoOfusers);
        }

        @Override
        public void onClick(View v) {
        }

    }
}

