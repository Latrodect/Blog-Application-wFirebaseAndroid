package com.example.MyBlogApp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>
{
    Context mContext;
    List<Blog> mData;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);

        return new MyViewHolder(row);
    }

    public PostAdapter(Context mContext, List<Blog> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.HeaderRow.setText(mData.get(position).getHeader());
        Glide.with(mContext).load(mData.get(position).getCircle()).into(holder.profileImage);
        Glide.with(mContext).load(mData.get(position).getImage()).into(holder.imgPost);



    }

    @Override
    public int getItemCount() {
        return mData.size();

    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView HeaderRow;
        TextView EntryRow;
        ImageView imgPost;
        CircleImageView profileImage;

        public MyViewHolder(View itemView){
            super(itemView);
            HeaderRow = itemView.findViewById(R.id.PostRowHeader);
            imgPost = itemView.findViewById(R.id.PostImageR);
            profileImage = itemView.findViewById(R.id.profilePostImgR);
            EntryRow = itemView.findViewById(R.id.DescPostRow);
        }
    }
}
