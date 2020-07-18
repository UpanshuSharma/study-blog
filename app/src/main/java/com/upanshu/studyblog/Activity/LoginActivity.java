package com.upanshu.studyblog.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upanshu.studyblog.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class LoginActivity extends AppCompatActivity {

    private EditText password, email;
    private Button login;
    private CircleImageView imageView;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        imageView=findViewById(R.id.login_profile_image);
        progressBar=findViewById(R.id.login_progressBar);
        login=findViewById(R.id.login_btn);
        //


        progressBar.setVisibility(View.INVISIBLE);

        mAuth=FirebaseAuth.getInstance();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login.setVisibility(View.INVISIBLE);


                final String userEmail=email.getText().toString();
                final String userPassword=password.getText().toString();

                if(userEmail.isEmpty() || userPassword.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please fill all the required Field", Toast.LENGTH_SHORT).show();
                    login.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    signIn(userEmail,userPassword);
                }
            }
        });

    }

    private void signIn(String userEmail, String userPassword)
    {

       mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {

               if(task.isSuccessful())
               {
                   progressBar.setVisibility(View.INVISIBLE);
                   login.setVisibility(View.VISIBLE);
                   updateUI();

               }
               else{

                   Toast.makeText(LoginActivity.this, "No such User found ! Register your self", Toast.LENGTH_SHORT).show();
                   login.setVisibility(View.VISIBLE);
                   progressBar.setVisibility(View.INVISIBLE);
               }
           }
       });

    }

    private void updateUI()
    {
        Intent intent=new Intent(this, MainActivity.class)  ;
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user !=null){
            // no need to login ....user already Connected
            updateUI();
        }

    }
}
