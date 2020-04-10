package com.example.MyBlogApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class Posts extends AppCompatActivity {
private Button ProfileButt;
String userId;
private Button HelpButt;
private Button NewPostButt;
private Button FullPostButt;
private RecyclerView mBlogList;
private DatabaseReference mDataBase;
private Query query;
private TextView mail,names,phones;
private String Ids;
private DatabaseReference mCurrentUser;
private  FirebaseAuth.AuthStateListener aulistener;
private Button EditTextButt;
private CircleImageView profileImage;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    static Uri cr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);


        mAuth =FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        cr = currentUser.getPhotoUrl();

        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Blog")
                .limitToLast(50);
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Blog");


        FirebaseRecyclerOptions<Blog> options =
                new FirebaseRecyclerOptions.Builder<Blog>()
                        .setQuery(query, Blog.class)
                        .build();



        mBlogList = (RecyclerView) findViewById(R.id.RecylerView);
        mBlogList.setHasFixedSize(true);
        LinearLayoutManager LM = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mBlogList.setLayoutManager(LM);
        ProfileButt = (Button) findViewById(R.id.ProfileButt);
        NewPostButt = (Button) findViewById(R.id.NewPostButt);
        HelpButt = (Button) findViewById(R.id.help_button_Two);
        FullPostButt = findViewById(R.id.FullPost);
        EditTextButt = (Button) findViewById(R.id.EditText);
        EditTextButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent OpenEditProfile = new Intent(getApplicationContext(),Authentication.class);
                startActivity(OpenEditProfile);
            }
        });

        FullPostButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rowPost = new Intent(getApplicationContext(),Screen.class);
                startActivity(rowPost);
            }
        });



        HelpButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Posts.this, "Profil sayfanıza hoş geldiniz.", Toast.LENGTH_SHORT).show();
            }
        });
        ProfileButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Posts.class));
            }
        });


        updateProfile();

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Blog> options =
                new FirebaseRecyclerOptions.Builder<Blog>()
                        .setQuery(mDataBase, Blog.class)
                        .build();
         FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull Blog model) {
                holder.setHeader(model.getHeader());
                holder.setDesc(model.getEntry());
                holder.setImage(getApplicationContext(),model.getImage());
                holder.setImageProfile(getApplicationContext(),model.getImage());


            }

            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blogcard, parent, false);
                return new  BlogViewHolder(view);
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }



    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setHeader(String header){
            TextView post_header = (TextView) mView.findViewById(R.id.HeaderPost);
            post_header.setText(header);
        }public void setDesc(String entry){
            TextView post_entry = (TextView) mView.findViewById(R.id.PostDesc);
            post_entry.setText(entry);
        }
        public void setImage(Context ctx,String Image){
            ImageView post_Image = (ImageView) mView.findViewById(R.id.PostImage);
            Picasso.with(ctx).load(Image).into(post_Image);
        }public void setImageProfile(Context ctx,String Image){
            CircleImageView post_Images = (CircleImageView) mView.findViewById(R.id.profilePostImg);
            Glide.with(mView).load(cr).into(post_Images);
        }

    }
    public void updateProfile(){
        names = findViewById(R.id.setUserName);
        mail = findViewById(R.id.setEmailAdress);
        phones = findViewById(R.id.setPhoneNumber);
        profileImage = findViewById(R.id.profileImage);
        names.setText(currentUser.getDisplayName());
        mail.setText(currentUser.getEmail());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(profileImage);


    }
}

