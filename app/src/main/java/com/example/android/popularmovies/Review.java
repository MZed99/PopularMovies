package com.example.android.popularmovies;

/**
 * Created by Tryndamere on 29/03/2017.
 */

public class Review {
    private String mUserName;
    private String mReviewBody;

    Review(String userName,String reviewBody){
        mUserName=userName;
        mReviewBody=reviewBody;
    }

    public String getUserName(){
        return mUserName;
    }
    public String getReviewBody(){
        return mReviewBody;
    }
}
