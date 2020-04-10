package com.example.MyBlogApp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class Authentication extends AppCompatActivity {
    String Display_Name_url = null;
    String Profile_Image_url = null;
    String Profile_Email_Url =null;
    EditText displayName,Email,About,Biographi,Phone;
    FirebaseUser user;
    static int PreqCode = 3;
    static int REQUESTCODE = 3;
    Uri pickedImgUriT;
    CircleImageView UpdateUserPhoto;
    FloatingActionButton fabButtUpdate;
    FirebaseDatabase mDataBase;
    String uId;
    String bioStr,bioAbout,bioPhhone;
    DatabaseReference bioReference;
    TextView PostBio;
    static String Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);



        displayName= (EditText) findViewById(R.id.DisplayNameUpdate);
        Email= (EditText) findViewById(R.id.EmailUpdate);
        About= (EditText) findViewById(R.id.AboutUpdate);
        Biographi= (EditText) findViewById(R.id.Biographis);
        Phone= (EditText) findViewById(R.id.phoneNum);
        PostBio = (TextView) findViewById(R.id.BiographiPost);
        mDataBase = FirebaseDatabase.getInstance();
        fabButtUpdate = (FloatingActionButton) findViewById(R.id.UpdateProfileFab);
        UpdateUserPhoto = (CircleImageView) findViewById(R.id.profileImageUpdate);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uId=user.getUid();
        bioReference = mDataBase.getReference("Profile_Bio").child(uId).push();
        Key = bioReference.getKey();



        fabButtUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bioStr =  (String) Biographi.getText().toString();
                bioPhhone  = (String) About.getText().toString();
                bioAbout = (String) Phone.getText().toString();
                Profile_Biographi bio = new Profile_Biographi(bioStr,bioPhhone,bioAbout);
                bioReference.setValue(bio).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Authentication.this, "Biographi Update Succesfully", Toast.LENGTH_SHORT).show();
                    }
                });
                ProfileUpdate();
            }


        });

        UpdateUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();
                }else
                {
                    openGallery();
                }
            }
        });





        if (user != null ){
            if (user.getDisplayName() != null){
                displayName.setText(user.getDisplayName());
                Email.setText(user.getEmail());
                Glide.with(Authentication.this).load(user.getPhotoUrl()).into(UpdateUserPhoto);
            }

        }
    }public void ProfileUpdate(){
        Display_Name_url = displayName.getText().toString();
        Profile_Email_Url = Email.getText().toString();


        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(Display_Name_url).build();
        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Authentication.this, "Profile  Updated Succesfully", Toast.LENGTH_SHORT).show();
            }
        });

        UserProfileChangeRequest PhotoRequest = new UserProfileChangeRequest.Builder().setPhotoUri(pickedImgUriT).build();
        user.updateProfile((PhotoRequest));




    }


    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(Authentication.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Authentication.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please accept for Request Permission", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(Authentication.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PreqCode);
            }
        }else{
            openGallery();
        }

    }private void openGallery(){

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/**");
        startActivityForResult(galleryIntent,REQUESTCODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null){
            pickedImgUriT = data.getData();
            UpdateUserPhoto.setImageURI(pickedImgUriT);
        }
    }

}
