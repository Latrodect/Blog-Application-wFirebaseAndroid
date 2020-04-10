package com.example.MyBlogApp;

import androidx.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Register extends AppCompatActivity {


    private EditText mFullName,mPassword,mEmail,mPhone;
    private Button RegisterButton;
    ImageView ImgUserPhoto;
    static int PreqCode = 1;
    static int REQUESTCODE = 1;
    Uri pickedImgUri;
    private ProgressBar loadingProgress;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = (EditText) findViewById(R.id.fullName);
        mPassword = (EditText) findViewById(R.id.password);
        mEmail = (EditText) findViewById(R.id.email);
        mPhone = (EditText) findViewById(R.id.phoneNum);
        loadingProgress = findViewById(R.id.regProgressBar);
        RegisterButton = (Button) findViewById(R.id.RegisterButton);
        ImgUserPhoto = findViewById(R.id.regUserPhoto);



        mAuth = FirebaseAuth.getInstance();


        loadingProgress.setVisibility(View.INVISIBLE);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegisterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RegisterButton.setVisibility(View.INVISIBLE);
                        loadingProgress.setVisibility(View.VISIBLE);
                        final String email =mEmail.getText().toString();
                        final String phone = mPhone.getText().toString();
                        final String name = mFullName.getText().toString();
                        final String password = mPassword.getText().toString();

                        if (email.isEmpty() || name.isEmpty() ||phone.isEmpty() || password.isEmpty()){

                            showMassage("Please Verify all fields.");
                            RegisterButton.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);

                        }else{
                            CreateUserAccount(email,name,password,phone);
                        }
                    }
                });



            }
        });

        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
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



    }

    private void CreateUserAccount(String email, final String name, String password, String phone) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    showMassage("Account Created.");

                    updateUserInfo(name,pickedImgUri,mAuth.getCurrentUser());
                }else{
                    showMassage("Account Creation Failed");
                    RegisterButton.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    showMassage("Register Completa");
                                    updateUI();
                                }


                            }
                        });

                    }
                })     ;

            }
        });

    }

    private void updateUI() {

        Intent PostIntent = new Intent(getApplicationContext(),Posts.class);
        startActivity(PostIntent);

    }

    private void showMassage(String s) {

        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Register.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please accept for Request Permission", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(Register.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PreqCode);
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
            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);
        }
    }
}
