package com.cxx.retrofitdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cxx.retrofitdemo.R;
import com.cxx.retrofitdemo.bean.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 陈宣宣 on 2016/11/24.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Movie> moviesList;

    public MoviesAdapter(Context context, List<Movie> moviesList){
        this.context = context;
        this.moviesList = (ArrayList<Movie>) moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movies,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvTitle.setText(moviesList.get(position).getTitle());
        holder.tvCount.setText("收藏数："+moviesList.get(position).getCollect_count());
        holder.tvYear.setText(moviesList.get(position).getYear());
        Glide.with(context).load(moviesList.get(position).getImages().get("medium")).into(holder.ivImage);//Glide加载网络图片


    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.iv_image)
        ImageView ivImage;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_collectcount)
        TextView tvCount;
        @Bind(R.id.tv_year)
        TextView tvYear;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
