package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Tryndamere on 22/01/2017.
 */

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.ViewHolder>{
    private ArrayList<Film> film=new ArrayList<>();
    private Context mContext;
    private final FilmAdapterOnClickHandler mClickHandler;


    public interface FilmAdapterOnClickHandler{
        void onClick(Film filmData);
    }

    public void setFilmData(ArrayList<Film> films){
        film=films;
        notifyDataSetChanged();
    }

    public FilmAdapter(FilmAdapterOnClickHandler clickHandler,Context context){
        mClickHandler=clickHandler;
        this.mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.film_item, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Picasso.with(mContext).load(film.get(i).getThumbnailString()).fit().into(viewHolder.thumb_image);
    }

    @Override
    public int getItemCount() {
        if (film!=null){
            return film.size();
        }else return 0;

    }

    //defines the views utilized by the adapter
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView thumb_image;

        public ViewHolder(View view) {
            super(view);
            thumb_image = (ImageView) view.findViewById(R.id.film_thumbnail);
            view.setOnClickListener(this);
        }
        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Film filmData=film.get(adapterPosition);
            mClickHandler.onClick(filmData);


        }

    }


}
