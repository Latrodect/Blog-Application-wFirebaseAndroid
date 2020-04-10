package com.example.MyBlogApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;

public class Login extends AppCompatActivity {
private EditText mEmails, mPasswords;
private Button LoginButton;
private ProgressBar loadingProgress;
private FirebaseAuth mAuth;
private Intent PostIntent;
private Button GoLoginButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Thread t = new Thread(){
            public void run(){
                try{
                    while(!isInterrupted()){
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView xtime = (TextView) findViewById(R.id.ttime);
                                long times = System.currentTimeMillis();
                                SimpleDateFormat sdf_t = new SimpleDateFormat("hh:mm");
                                String dateString = sdf_t.format(times);
                                xtime.setText(dateString);
                            }
                        });
                    }
                }catch (InterruptedException e){

                }
            }
        };
        t.start();

        mEmails = (EditText) findViewById(R.id.emailLogin);
        mPasswords = (EditText) findViewById(R.id.passwordLogin);
        LoginButton = (Button) findViewById(R.id.LoginButton);
        loadingProgress = findViewById(R.id.loginProgress);
        GoLoginButton = (Button) findViewById(R.id.GoLog);
        mAuth = FirebaseAuth.getInstance();
        PostIntent =  new Intent(this,Posts.class);

        loadingProgress.setVisibility(View.INVISIBLE);

        GoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingProgress.setVisibility(View.VISIBLE);
                LoginButton.setVisibility(View.INVISIBLE);
                GoLoginButton.setVisibility(View.INVISIBLE);

                final String email = mEmails.getText().toString();
                final String password = mPasswords.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    loadingProgress.setVisibility(View.INVISIBLE);
                    GoLoginButton.setVisibility(View.VISIBLE);
                    LoginButton.setVisibility(View.VISIBLE);
                    showMassage("Please Verify Email and Password");
                }else{
                    signIn(email,password);
                }
            }
        });

    }

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    loadingProgress.setVisibility(View.INVISIBLE);
                    LoginButton.setVisibility(View.VISIBLE);
                    updateUI();
                }else{
                    loadingProgress.setVisibility(View.INVISIBLE);
                    showMassage(task.getException().getMessage());
                    LoginButton.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private void updateUI() {

        startActivity(PostIntent);
        finish();

    }

    private void showMassage(String please_verify_email_and_password) {

        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            updateUI();
        }

    }
}



