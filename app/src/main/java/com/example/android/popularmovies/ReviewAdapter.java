package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tryndamere on 29/03/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<Review> review=new ArrayList<>();
    private Context mContext;

    public ReviewAdapter(Context context){
        this.mContext=context;
    }
    private Context getContext(){
        return mContext;
    }
    public void setReviewData(ArrayList<Review> reviews){
        review=reviews;
        notifyDataSetChanged();
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View reviewsView = inflater.inflate(R.layout.review_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(reviewsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        Review singleReview = review.get(position);
        TextView userName= holder.userName;
        userName.setText(singleReview.getUserName());

        TextView bodyReview  = holder.reviewBody;
        bodyReview.setText(singleReview.getReviewBody());

    }

    @Override
    public int getItemCount() {
        return review.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userName;
        public TextView reviewBody;

        public ViewHolder(View itemView){
            super(itemView);
            userName=(TextView) itemView.findViewById(R.id.user_review_name);
            reviewBody=(TextView) itemView.findViewById(R.id.user_review_body);
        }
    }
}
