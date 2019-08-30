package com.example.surya.knitconfession;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView profile_username,profile_college,profile_DOB,profile_status,profile_relationalStatus,profile_year,profile_gender,friendlist;
    private CircleImageView profile_profileImage;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference profileRef;
    private String current_user_id;
    private ImageView editImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        friendlist=(TextView) findViewById(R.id.friendsList);
        friendlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendTOfriendsActivity();
            }
        });
        //Profile editView
        editImage=(ImageView) findViewById(R.id.profileeditView);
        profile_username=(TextView) findViewById(R.id.profile_username);
        profile_college=(TextView) findViewById(R.id.profile_College);
        profile_DOB=(TextView) findViewById(R.id.profile_DOB);
        profile_status=(TextView) findViewById(R.id.profile_Status);
        profile_relationalStatus=(TextView) findViewById(R.id.profile_Relationship_Satus);
        profile_year=(TextView) findViewById(R.id.profile_Year);
        profile_gender=(TextView) findViewById(R.id.profile_Gender);
        profile_profileImage=(CircleImageView) findViewById(R.id.profile_profileImage);
        //Toolbar
        mToolbar=(Toolbar) findViewById(R.id.appBar_profileActivity);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent=new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        //Profile Edtibar
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent=new Intent(ProfileActivity.this,SettingActivity.class);
                //settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(settingIntent);
                finish();
            }
        });

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        profileRef= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String username=dataSnapshot.child("username").getValue().toString();
                    String userstatus=dataSnapshot.child("Status").getValue().toString();
                    String userrelStatus=dataSnapshot.child("Relationship Status").getValue().toString();
                    String useryear=dataSnapshot.child("useryear").getValue().toString();
                    String profileimage=dataSnapshot.child("profileImage").getValue().toString();
                    String usercollege=dataSnapshot.child("usercollege").getValue().toString();
                    String userDob=dataSnapshot.child("DOB").getValue().toString();
                    String userGender=dataSnapshot.child("Gender").getValue().toString();
                    Picasso.with(ProfileActivity.this).load(profileimage).placeholder(R.drawable.us).into(profile_profileImage);
                    profile_username.setText(username);
                    profile_college.setText(userstatus);
                    profile_DOB.setText(userDob);
                    profile_status.setText(userstatus);
                    profile_relationalStatus.setText(userrelStatus);
                    profile_gender.setText(userGender);
                    profile_college.setText(usercollege);
                    profile_year.setText(useryear);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void SendTOfriendsActivity() {
        Intent senTofriendsIntent=new Intent(ProfileActivity.this,FriendsActivity.class);
        senTofriendsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(senTofriendsIntent);
        finish();
    }

}
