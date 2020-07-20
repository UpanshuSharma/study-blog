package com.upanshu.studyblog.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.upanshu.studyblog.R;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private FirebaseUser currentUser;
    FirebaseAuth mAuth;

    Dialog popAddPost;
    ImageView popupPostImage,popUpAddBtn;
     CircleImageView popUpUserImage;
    EditText popUpTitle,popUpDescription;
    ProgressBar popUpProgressBar;

    static  int reqCode=2;
    static int REQUESTCODE=2;

    private AppBarConfiguration mAppBarConfiguration;
    private Uri imageUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //..............
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        //.........................

        /*Getting the PopUp Window*/
        iniPop();
        setupPopupImageClick();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_new_post, R.id.nav_notification,
                R.id.nav_profile, R.id.nav_setting)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        updateNavHeader();
    }

    private void setupPopupImageClick() {

        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open Gallery

                    CheckAndRequestForPermission();



            }
        });

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
             popupPostImage.setImageURI(imageUri);




        }
        else{
            Toast.makeText(this, "Error !!  Try Again", Toast.LENGTH_SHORT).show();
            startActivity (new Intent(MainActivity.this,RegisterActivity.class));
            finish();
        }
    }

    private void CheckAndRequestForPermission() {


        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, " Please accept  for required Permission", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},reqCode);

            }

        }
        else{
            OpenGallery();
        }



    }

    /* Getting the Popup Window*/
    //............................................................
    private void iniPop(){
        popAddPost=new Dialog(this);
        popAddPost.setContentView(R.layout.pop_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity= Gravity.TOP;

        popUpUserImage=popAddPost.findViewById(R.id.popup_user_pic);
        popUpTitle=popAddPost.findViewById(R.id.editText);
        popupPostImage=popAddPost.findViewById(R.id.popup_user_post_pic);
        popUpDescription=popAddPost.findViewById(R.id.editText2);
        popUpProgressBar=popAddPost.findViewById(R.id.popup_progressBar);
        popUpAddBtn=popAddPost.findViewById(R.id.popup_user_pic);

        Picasso.get().load(currentUser.getPhotoUrl()).placeholder(R.drawable.profilelogo1).into(popUpUserImage);


        //popUpAddBtn ClickListner
        popUpAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpAddBtn.setVisibility(View.INVISIBLE);
                popUpProgressBar.setVisibility(View.VISIBLE);

                if( !popUpTitle.getText().equals("") && !popUpDescription.getText().equals("")
                && imageUri !=null){
                    //Working well everything add it this post firebase

                }
                else{
                    Toast.makeText(MainActivity.this, "Please provides Title, description or PostImage", Toast.LENGTH_SHORT).show();
                    popUpProgressBar.setVisibility(View.INVISIBLE);
                    popUpAddBtn.setVisibility(View.VISIBLE);
                }
            }
        });


    }
    //.................................................................

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this,LoginActivity.class));
             finish();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView=navigationView.getHeaderView(0);
        TextView username=headerView.findViewById(R.id.user_profile_name);
        TextView useremail=headerView.findViewById(R.id.user_profile_email);
        CircleImageView userimage=headerView.findViewById(R.id.user_profile_image);

        useremail.setText(currentUser.getEmail());
        username.setText(currentUser.getDisplayName());
        Picasso.get().load(currentUser.getPhotoUrl()).placeholder(R.drawable.profile).into(userimage);

    }
}
