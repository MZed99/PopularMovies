package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tryndamere on 22/01/2017.
 */


public class Film implements Parcelable {
    private String mFilmThumbnail;
    private String mOverview;
    private String mUsersRate;
    private String mTitle;
    private String mReleaseDate;
    private String mId;


    //A Film object holds a thumbnail and an overview
    public Film(String filmThumbnail, String filmOverview,String filmUsersRate,String filmTitle,String filmReleaseDate,String id) {
        mFilmThumbnail = filmThumbnail;
        mOverview = filmOverview;
        mUsersRate=filmUsersRate;
        mTitle= filmTitle;
        mReleaseDate=filmReleaseDate;
        mId=id;

    }

    //methods that can be called on Film object, to return its contents
    public String getThumbnailString() {
        return mFilmThumbnail;
    }

    public String getOverview() {
        return mOverview;
    }
    public String getUsersRate() {
        return mUsersRate;
    }
    public String getTitle() {
        return mTitle;
    }
    public String getReleaseDate() {
        return mReleaseDate;
    }
    public String getId(){
        return mId;
    }


    //The following lines implements parcelable, to make it possible to pass the arraylist via intent
    public Film(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        mFilmThumbnail = data[0];
        mOverview = data[1];
        mUsersRate = data[2];
        mTitle = data[3];
        mReleaseDate = data[4];
        mId = data[5];

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{mFilmThumbnail, mOverview,mUsersRate,mTitle,mReleaseDate,mId});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Film createFromParcel(Parcel in) {
            return new Film(in);
        }

        public Film[] newArray(int size) {
            return new Film[size];
        }
    };
}

