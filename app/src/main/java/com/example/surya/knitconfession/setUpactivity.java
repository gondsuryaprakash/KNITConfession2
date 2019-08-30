package com.example.surya.knitconfession;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
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

public class setUpactivity extends AppCompatActivity {
    private EditText userName,UserCollege,UserYear;
    private Button save;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private ProgressDialog loadingbar;
    String current_User_Id;
    final static int pick_size=1;
    private StorageReference userProfileRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        current_User_Id=mAuth.getCurrentUser().getUid();
        loadingbar=new ProgressDialog(this);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_User_Id);
        userProfileRef= FirebaseStorage.getInstance().getReference().child("profile Image");
        setContentView(R.layout.activity_set_upactivity);
        userName =(EditText) findViewById(R.id.userName);
        UserCollege =(EditText) findViewById(R.id.userCollege);
        UserYear =(EditText) findViewById(R.id.userYear);
        save =(Button) findViewById(R.id.button_setupprofile);
        profileImage=(CircleImageView) findViewById(R.id.userprofilePic);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, pick_size);
            }


        });
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileImage")){
                        String image=dataSnapshot.child("profileImage").getValue().toString();
                        Picasso.with(setUpactivity.this).load(image).placeholder(R.drawable.us).into(profileImage);
                    }
                     else{
                        Toast.makeText(setUpactivity.this,"Please select profile Image first...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_Account_info();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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
                StorageReference filepath=userProfileRef.child(current_User_Id +".jpg");
                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(setUpactivity.this,"Profile Pic Saved",Toast.LENGTH_SHORT).show();
                            final String downloadurl=task.getResult().getDownloadUrl().toString();
                            userRef.child("profileImage").setValue(downloadurl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent selfIntent=new Intent(setUpactivity.this,setUpactivity.class);
                                                startActivity(selfIntent);
                                                finish();
                                                Toast.makeText(setUpactivity.this,"Just save Image",Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                            else{
                                                String message=task.getException().getMessage();
                                                Toast.makeText(setUpactivity.this,"Error Occured"+message,Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                        }
                                    });

                        }

                        else{
                            String message=task.getException().getMessage();
                            Toast.makeText(setUpactivity.this,"Error Occured"+message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(setUpactivity.this,"Error Occured:Image can't be croped",Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
            }
        }
    }
    private void save_Account_info() {
        String username =userName.getText().toString();
        String usercollege=UserCollege.getText().toString();
        String useryear=UserYear.getText().toString();
        if(TextUtils.isEmpty(username)){
            Toast.makeText(setUpactivity.this,"Please write the User Name..",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(usercollege)){
            Toast.makeText(setUpactivity.this,"Please write the User Name..",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(useryear)){
            Toast.makeText(setUpactivity.this,"Please write the User Name..",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Saving");
            loadingbar.setMessage("saving your info please wait...");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            HashMap usermap=new HashMap();
            usermap.put("username",username);
            usermap.put("usercollege",usercollege);
            usermap.put("useryear",useryear);
            usermap.put("Status","Hey I'm useing KNIT confession");
            usermap.put("Gender","None");
            usermap.put("DOB","None");
            usermap.put("Relationship Status","None");
            userRef.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        sendTomainActivity();
                        Toast.makeText(setUpactivity.this,"Succesfully updated to your Account Details",Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                    else{
                        String message=task.getException().getMessage();
                        Toast.makeText(setUpactivity.this, "Error Occured:"+ message, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }

                }
            });
        }
    }

    private void sendTomainActivity() {
        Intent mainIntent=new Intent(setUpactivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
