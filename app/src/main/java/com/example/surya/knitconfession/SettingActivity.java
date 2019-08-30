package com.example.surya.knitconfession;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText settingusername,settingcollege,settinggender,settingrelationship,settingstatus,settingyear,settingDob;
    private Button updatesettinginfobutton;
    private CircleImageView profileimageView;
    private DatabaseReference SettingRef;
    private FirebaseAuth mAuth;
    private String currentusers;
    private ProgressDialog loadingbar;
    final static int pick_size=1;
    private StorageReference userProfileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        loadingbar=new ProgressDialog(this);
        //Firebase
        mAuth=FirebaseAuth.getInstance();
        currentusers=mAuth.getCurrentUser().getUid();
        SettingRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentusers);
        userProfileRef= FirebaseStorage.getInstance().getReference().child("profile Image");
        SettingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage=dataSnapshot.child("profileImage").getValue().toString();
                    String myUsername=dataSnapshot.child("username").getValue().toString();
                    String myDOB=dataSnapshot.child("DOB").getValue().toString();
                    String myCollege=dataSnapshot.child("usercollege").getValue().toString();
                    String myStatus=dataSnapshot.child("Status").getValue().toString();
                    String myGender=dataSnapshot.child("Gender").getValue().toString();
                    String myrelationshipStatus=dataSnapshot.child("Relationship Status").getValue().toString();
                    String myYear=dataSnapshot.child("useryear").getValue().toString();
                    //setDataintoView
                    Picasso.with(SettingActivity.this).load(myProfileImage).placeholder(R.drawable.us).into(profileimageView);
                    settingusername.setText(myUsername);
                    settingDob.setText(myDOB);
                    settingcollege.setText(myCollege);
                    settingstatus.setText(myStatus);
                    settinggender.setText(myGender);
                    settingrelationship.setText(myrelationshipStatus);
                    settingyear.setText(myYear);
                    //UPDATE BUTTON
                    updatesettinginfobutton=(Button) findViewById(R.id.setting_update_button);
                    updatesettinginfobutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            validatePost();

                        }
                    });






                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //ToolBar
        mToolbar = (Toolbar) findViewById(R.id.setting_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Setting");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent=new Intent(SettingActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        //EDITTEXT
        settingDob=(EditText) findViewById(R.id.setting_DOB);
        settingusername =(EditText) findViewById(R.id.setting_username);
        settingcollege =(EditText) findViewById(R.id.setting_collegename);
        settinggender =(EditText) findViewById(R.id.setting_Gender);
        settingrelationship =(EditText) findViewById(R.id.setting_relationshipstatus);
        settingstatus =(EditText) findViewById(R.id.setting_status);
        settingyear =(EditText) findViewById(R.id.setting_Year);
        updatesettinginfobutton=(Button) findViewById(R.id.setting_update_button);
        //CircleImageView;
        profileimageView=(CircleImageView) findViewById(R.id.setting_profileImage);
        profileimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, pick_size);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==pick_size && resultCode==RESULT_OK && data!=null){
            Uri imageuri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);


        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result =CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                loadingbar.setTitle("Image Uploading");
                loadingbar.setMessage("please wait while we saving your profile pic...");
                loadingbar.setCanceledOnTouchOutside(true);
                loadingbar.show();
                Uri resulturi=result.getUri();
                StorageReference filepath=userProfileRef.child(currentusers +".jpg");
                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SettingActivity.this,"Profile Pic Saved",Toast.LENGTH_SHORT).show();
                            final String downloadurl=task.getResult().getDownloadUrl().toString();
                            SettingRef.child("profileImage").setValue(downloadurl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent selfIntent=new Intent(SettingActivity.this,SettingActivity.class);
                                                startActivity(selfIntent);
                                                finish();
                                                Toast.makeText(SettingActivity.this,"Just save Image",Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                            else{
                                                String message=task.getException().getMessage();
                                                Toast.makeText(SettingActivity.this,"Error Occured"+message,Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                        }
                                    });

                        }

                        else{
                            String message=task.getException().getMessage();
                            Toast.makeText(SettingActivity.this,"Error Occured"+message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(SettingActivity.this,"Error Occured:Image can't be croped",Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
            }
        }
    }

    private void validatePost() {
        String username=settingusername.getText().toString();
        String userdob=settingDob.getText().toString();
        String usercollege=settingcollege.getText().toString();
        String userstatus=settingstatus.getText().toString();
        String userrelstatus=settingrelationship.getText().toString();
        String usergender=settinggender.getText().toString();
        String useryear=settingyear.getText().toString();
        if(TextUtils.isEmpty(username)){
            Toast.makeText(SettingActivity.this,"Please fill username",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(userdob)){
            Toast.makeText(SettingActivity.this,"Please fill DOB",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(usercollege)){
            Toast.makeText(SettingActivity.this,"Please fill college",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userstatus)){
            Toast.makeText(SettingActivity.this,"Please fill status",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userrelstatus)){
            Toast.makeText(SettingActivity.this,"Please fill Relationship Status",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(usergender)){
            Toast.makeText(SettingActivity.this,"Please fill Gender",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(useryear)){
            Toast.makeText(SettingActivity.this,"Please fill Year",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Updating User Data");
            loadingbar.setMessage("please wait while we saving your profile pic...");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            updateUserInfo(username,userdob,usercollege,useryear,userstatus,userrelstatus,usergender);
        }


    }

    private void updateUserInfo(String username, String userdob, String usercollege, String useryear, String userstatus, String userrelstatus, String usergender) {

        HashMap usermap=new HashMap();
        usermap.put("username",username);
        usermap.put("DOB",userdob);
        usermap.put("usercollege",usercollege);
        usermap.put("useryear",useryear);
        usermap.put("Status",userstatus);
        usermap.put("Relationship Status",userrelstatus);
        usermap.put("Gender",usergender);
        SettingRef.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    SendtomainActivity();
                    Toast.makeText(SettingActivity.this,"Account details updated succefully",Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
                else {
                    String message=task.getException().getMessage();
                    Toast.makeText(SettingActivity.this,"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }
        });
    }

    private void SendtomainActivity() {
        Intent mainIntent=new Intent(SettingActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
