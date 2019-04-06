package com.example.cs160_sp18.prog3;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import static com.example.cs160_sp18.prog3.LandmarkAdapter.mContext;

public class LandmarkAdapter extends RecyclerView.Adapter {

    public static Context mContext;
    private ArrayList<Landmarks> landmarks;

    public LandmarkAdapter(Context context, ArrayList<Landmarks> bears) {
        mContext = context;
        landmarks = bears;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.landmark_layout, parent, false);
        return new LandmarkHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Landmarks item = landmarks.get(position);
        ((LandmarkHolder) holder).bind(item);
    }

    @Override
    public int getItemCount() {
        return landmarks.size();
    }
}

class LandmarkHolder extends RecyclerView.ViewHolder {
    public RelativeLayout mLayout;
    public TextView location;
    public TextView dist;
    public ImageView photo;



    public LandmarkHolder(View itemView) {
        super(itemView);
        mLayout = itemView.findViewById(R.id.landmark_layout);
        photo = mLayout.findViewById(R.id.photo);
        location = mLayout.findViewById(R.id.locationText);
        dist = mLayout.findViewById(R.id.distance);



        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dist.getText() == "Less than 10 meters away") {
                    Intent next = new Intent(mContext, CommentFeedActivity.class);
                    next.putExtra("username", SecondActivity.username);
                    next.putExtra("locationName", location.getText());
                    mContext.startActivity(next);

                } else {
                    Toast.makeText(mContext, "Must be within 10 meters of a landmark to comment", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    void bind(Landmarks l) {
        location.setText(l.name);
        dist.setText(l.dist);
        int id = mContext.getResources().getIdentifier(l.pic, "drawable",
                mContext.getPackageName());
        photo.setImageResource(id);
    }


}



