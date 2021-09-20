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

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.MyViewHolder> {

    Context context;
    private ArrayList<SurveyListModel> dataModelArrayList;
    private LayoutInflater inflater;

    public DashboardAdapter(Context ctx, ArrayList<SurveyListModel> dataModelArrayList ){
        context=ctx;
        inflater = LayoutInflater.from(ctx);
        this.dataModelArrayList = dataModelArrayList;
    }

    @Override
    public DashboardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.surveyadapter1, parent, false);
        DashboardAdapter.MyViewHolder holder = new DashboardAdapter.MyViewHolder(view);
        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(DashboardAdapter.MyViewHolder holder, final int position) {
        holder.nametv.setText(dataModelArrayList.get(position).getFormName());
        holder.prospectives.setText(dataModelArrayList.get(position).getNoofRegistrations());

    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nametv;
        TextView prospectives;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nametv = itemView.findViewById(R.id.nametv);
            prospectives = itemView.findViewById(R.id.prospectives);
        }

        @Override
        public void onClick(View v) {
        }

    }
}

