package com.example.MyBlogApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Screen extends AppCompatActivity {
    Dialog popAddPost;
    EditText Header;
    EditText Entry;
    FloatingActionButton Share;
    Button HelpButton;
    ImageButton AddImage;
    CircleImageView profileAdmin;
    static int PreqCode = 2;
    FloatingActionButton floatingActionButton;
    static int REQUESTCODE = 2;
    Uri pickedImgUri = null;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    ProgressDialog mProgress;
    DatabaseReference mDataBase;
    FloatingActionButton fabBut;
    private RecyclerView mBlogLisRt;
    private Query query;
    static Uri cr;
    static Context mContext;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Blog")
                .limitToLast(50);

        FirebaseRecyclerOptions<Blog> options =
                new FirebaseRecyclerOptions.Builder<Blog>()
                        .setQuery(query, Blog.class)
                        .build();

        mContext = getApplicationContext();
        mBlogLisRt = (RecyclerView) findViewById(R.id.postRecyle);
        mBlogLisRt.setHasFixedSize(true);
        LinearLayoutManager LM = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mBlogLisRt.setLayoutManager(LM);

        mAuth =FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        cr = currentUser.getPhotoUrl();
        initPopup();
        setupImagePicker();
        mProgress = new ProgressDialog(this);
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Blog");

        fabBut = (FloatingActionButton) findViewById(R.id.fab);
        fabBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabBut.setVisibility(View.INVISIBLE);
                popAddPost.show();
            }
        });


        getSupportFragmentManager().beginTransaction().add(R.id.postRec,new RowFragment(),"jjjj");

    }

    private void setupImagePicker() {

        AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }
        });

    }

    private void initPopup() {




        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        AddImage = (ImageButton) popAddPost.findViewById(R.id.AddImage);
        Header = (EditText) popAddPost.findViewById(R.id.Header);
        Header.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        Entry = (EditText) popAddPost.findViewById(R.id.Entry);
        Entry.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        Share = (FloatingActionButton) popAddPost.findViewById(R.id.Share);
        HelpButton = (Button) popAddPost.findViewById(R.id.help_button_one);
        profileAdmin = popAddPost.findViewById(R.id.profileImage);
        Glide.with(this).load(currentUser.getPhotoUrl()).into(profileAdmin);

        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Share.setVisibility(View.INVISIBLE);

                if (!TextUtils.isEmpty(Entry.getText().toString()) && !TextUtils.isEmpty(Header.getText().toString()) && pickedImgUri != null){

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    final StorageReference filepath  = storageReference.child("Blog_Images").child(pickedImgUri.getLastPathSegment());
                    filepath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String downloadUrl = uri.toString();
                                    DatabaseReference newPost =mDataBase.push();
                                    newPost.child("header").setValue(Header.getText().toString().trim());
                                    newPost.child("entry").setValue(Entry.getText().toString().trim());
                                    newPost.child("image").setValue(downloadUrl);
                                    newPost.child("userid").setValue(currentUser.getUid());
                                    newPost.child("prof").setValue(currentUser.getPhotoUrl().toString());
                                    newPost.child("timestamp").setValue(ServerValue.TIMESTAMP);
                                    popAddPost.dismiss();



                                }
                            });

                        }
                    });


                }else{
                    showMassage("Please verify all input fields and chose Post Image");
                }
            }
        });

    }


    private void showMassage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please accept for Request Permission", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PreqCode);
            }
        }else{
            openGallery();
        }

    }private void openGallery(){

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/**");
        startActivityForResult(galleryIntent,REQUESTCODE);

    }@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null){
            pickedImgUri = data.getData();
            AddImage.setImageURI(pickedImgUri);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Blog> options =
                new FirebaseRecyclerOptions.Builder<Blog>()
                        .setQuery(mDataBase, Blog.class)
                        .build();
        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull Blog model) {
                final  String post_key = getRef(position).getKey();
                holder.setHeader(model.getHeader());
                holder.setDesc(model.getEntry());
                holder.setImage(getApplicationContext(),model.getImage());
                holder.setImageProfile(getApplicationContext(),model.getCircle());


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         Intent DetailPost = new Intent(Screen.this, com.example.MyBlogApp.DetailPost.class);
                         DetailPost.putExtra("blog_id",post_key);
                         startActivity(DetailPost);
                    }
                });




            }

            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_post_item, parent, false);
                return new BlogViewHolder(view);

            }
        };
        mBlogLisRt.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


        }
        public void setHeader(String header){
            TextView post_header = (TextView) mView.findViewById(R.id.PostRowHeader);
            post_header.setText(header);
        }public void setDesc(String entry){
            TextView post_entry = (TextView) mView.findViewById(R.id.DescPostRow);
            post_entry.setText(entry);
        }
        public void setImage(Context ctx, String Image){
            ImageView post_Image = (ImageView) mView.findViewById(R.id.PostImageR);
            Picasso.with(ctx).load(Image).into(post_Image);
        }public void setImageProfile(Context ctx,String Circle){
            CircleImageView post_Images = (CircleImageView) mView.findViewById(R.id.profilePostImgR);
            Glide.with(mView).load(cr).into(post_Images);
        }

    }
}
