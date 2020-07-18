package com.upanshu.studyblog.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.upanshu.studyblog.R;
import de.hdodenhof.circleimageview.CircleImageView;
public class RegisterActivity extends AppCompatActivity {

    private CircleImageView userProfilePic;
    private EditText username, email, password;
    private Button registerButton;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private TextView alreadyHaveAccount;
    static  int reqCode=1;
    static int REQUESTCODE=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username=findViewById(R.id.register_name);
        email=findViewById(R.id.register_email);
        password=findViewById(R.id.register_password);
        registerButton=findViewById(R.id.register_btn);
        alreadyHaveAccount=findViewById(R.id.register_alreadyhaveAnAccount);
        userProfilePic=findViewById(R.id.register_profile_image);

        mAuth=FirebaseAuth.getInstance();

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=22){
                    CheckAndRequestForPermission();
                }
                else{
                    OpenGallery();
                }


            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String uname=username.getText().toString();
                final String mail=email.getText().toString();
                final String pass=password.getText().toString();


                if(uname.isEmpty() || pass.isEmpty() || mail.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please provide all the  required fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog=new ProgressDialog(RegisterActivity.this);
                    progressDialog.setTitle("Creating Account");
                    progressDialog.setMessage("Please Wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    CreateAccount(uname, mail,pass);
                }
            }
        });
    }

    private void CreateAccount(final String uname, String mail, String pass) {
        mAuth.createUserWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Registration Successfully", Toast.LENGTH_SHORT).show();
                             updateUserInfo(uname,imageUri,mAuth.getCurrentUser());
                             progressDialog.dismiss();
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Registration Failed ", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                });

    }

    private void updateUserInfo(final String  uname, Uri imageUri, final FirebaseUser currentUser) {
        StorageReference mStorage= FirebaseStorage.getInstance().getReference().child("users_profiles_pic");
        final StorageReference imageFilepath=mStorage.child(imageUri.getLastPathSegment());
        imageFilepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 imageFilepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                     @Override
                     public void onSuccess(Uri uri) {
                         UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                         .setDisplayName(uname)
                         . setPhotoUri(uri)
                         .build();

                      currentUser.updateProfile(profileChangeRequest)
                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()){
                                  Toast.makeText(RegisterActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                                  UpdateUI();
                              }
                              else{
                                  Toast.makeText(RegisterActivity.this, " Error ", Toast.LENGTH_SHORT).show();
                              }

                          }
                      }) ;
                     }
                 });


            }
        });

    }

    private void UpdateUI() {
       Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
       startActivity(intent);
        finish();
    }


    private void OpenGallery() {
          Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
          galleryIntent.setType("image/*");
          startActivityForResult(galleryIntent,REQUESTCODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==REQUESTCODE && data!=null){
            imageUri=data.getData();
            userProfilePic.setImageURI(imageUri);




        }
        else{
            Toast.makeText(this, "Error !!  Try Again", Toast.LENGTH_SHORT).show();
            startActivity (new Intent(RegisterActivity.this,RegisterActivity.class));
            finish();
        }
    }

    private void CheckAndRequestForPermission() {


        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, " Please accept  for required Permission", Toast.LENGTH_SHORT).show();
            }
            else{
              ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},reqCode);

            }

        }
        else{
            OpenGallery();
        }



    }
}
