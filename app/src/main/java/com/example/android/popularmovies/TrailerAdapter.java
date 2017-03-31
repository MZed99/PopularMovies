package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tryndamere on 30/03/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder>{

    private ArrayList<String> mUrls=new ArrayList<String>();
    private Context mContext;
    private final TrailerAdapterOnClickHandler mClickHandler;

    public interface TrailerAdapterOnClickHandler{
        void onClick(String url);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler,Context context){
        this.mContext=context;
        mClickHandler=clickHandler;
    }
    public void setTrailerData(ArrayList<String> urls){
        mUrls=urls;
        notifyDataSetChanged();
    }
    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View trailersView = layoutInflater.inflate(R.layout.trailer_item,parent,false);
        return new ViewHolder(trailersView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView numberTrailer = holder.numbTrail;
        String positio=Integer.toString(position+1);
        numberTrailer.append(" "+positio);
    }

    @Override
    public int getItemCount() {
        return mUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView numbTrail;
        private ImageView playButton;
        public ViewHolder(View view){
            super(view);
            playButton = (ImageView) view.findViewById(R.id.trailer_playbutton);
            numbTrail = (TextView) view.findViewById(R.id.trailed_item_id);
            playButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String url=mUrls.get(adapterPosition);
            mClickHandler.onClick(url);
        }
    }
}
