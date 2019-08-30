package com.example.surya.knitconfession;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchPersonActivity extends AppCompatActivity {
    private TextView spprofile_username,spprofile_college,spprofile_DOB,spprofile_status,spprofile_relationalStatus,spprofile_year,spprofile_gender;
    private CircleImageView spprofile_profileImage;
    private Button sendrequestButton,cancelrequest;
    private FirebaseAuth mAuth;
    private DatabaseReference PersonRef,FriendsrequestRef,FriendRef;
    private String reciverUserId,senderUserId,current_state,saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_person);
        mAuth =FirebaseAuth.getInstance();
        //FriendsRef
        FriendRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsrequestRef=FirebaseDatabase.getInstance().getReference().child("FriendsRequest");
        sendrequestButton=(Button) findViewById(R.id.imagebuttonsendrequest);
        cancelrequest=(Button) findViewById(R.id.imagebuttoncancelsendrequest);
        castingelment();
        senderUserId=mAuth.getCurrentUser().getUid();

        reciverUserId=getIntent().getExtras().get("personkey").toString();
        PersonRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PersonRef.child(reciverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.exists()){
                        String spusername=dataSnapshot.child("username").getValue().toString();
                        String spuserstatus=dataSnapshot.child("Status").getValue().toString();
                        String spuserrelStatus=dataSnapshot.child("Relationship Status").getValue().toString();
                        String spuseryear=dataSnapshot.child("useryear").getValue().toString();
                        String spprofileimage=dataSnapshot.child("profileImage").getValue().toString();
                        String spusercollege=dataSnapshot.child("usercollege").getValue().toString();
                        String spuserDob=dataSnapshot.child("DOB").getValue().toString();
                        String spuserGender=dataSnapshot.child("Gender").getValue().toString();
                        Picasso.with(SearchPersonActivity.this).load(spprofileimage).placeholder(R.drawable.us).into(spprofile_profileImage);
                        spprofile_username.setText(spusername);
                        spprofile_college.setText(spuserstatus);
                        spprofile_DOB.setText(spuserDob);
                        spprofile_status.setText(spuserstatus);
                        spprofile_relationalStatus.setText(spuserrelStatus);
                        spprofile_gender.setText(spuserGender);
                        spprofile_college.setText(spusercollege);
                        spprofile_year.setText(spuseryear);

                        mainTainanceButton();


                    }
                }
                else{
                    Toast.makeText(SearchPersonActivity.this,"Error Occured..",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        cancelrequest.setVisibility(View.INVISIBLE);
        cancelrequest.setEnabled(false);
        if(!senderUserId.equals(reciverUserId)){
            sendrequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendrequestButton.setEnabled(false);
                    if(current_state.equals("not_friends")){
                        sendtofriendrequest();
                    }
                    if(current_state.equals("request_sent")){
                        canelFriendRequest();
                    }
                    if(current_state.equals("request_recived")){
                        AcceptRfriendsRequest();
                        cancelrequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                canelFriendRequest();
                            }
                        });
                    }
                    if(current_state.equals("friends")){
                        unFriend();
                    }
                }
            });

        }
        else {
            cancelrequest.setVisibility(View.INVISIBLE);
            sendrequestButton.setVisibility(View.INVISIBLE);

        }

    }

    private void unFriend() {
        FriendRef.child(senderUserId).child(reciverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRef.child(reciverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendrequestButton.setEnabled(true);
                                                current_state="not_friends";
                                                sendrequestButton.setText("Add Friends");
                                                cancelrequest.setVisibility(View.INVISIBLE);
                                                cancelrequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void AcceptRfriendsRequest() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());
        FriendRef.child(senderUserId).child(reciverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRef.child(reciverUserId).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FriendsrequestRef.child(senderUserId).child(reciverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    FriendsrequestRef.child(reciverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        sendrequestButton.setEnabled(true);
                                                                                        current_state="friends";
                                                                                        sendrequestButton.setText("Unfrienf");
                                                                                        cancelrequest.setVisibility(View.INVISIBLE);
                                                                                        cancelrequest.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                        }
                    }
                });



    }

    private void canelFriendRequest() {
        FriendsrequestRef.child(senderUserId).child(reciverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsrequestRef.child(reciverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendrequestButton.setEnabled(true);
                                                current_state="not_friends";
                                                sendrequestButton.setText("Add Friend");
                                                cancelrequest.setVisibility(View.INVISIBLE);
                                                cancelrequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void mainTainanceButton() {
        FriendsrequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(reciverUserId)){
                            String request_type=dataSnapshot.child(reciverUserId).child("request_type").getValue().toString();
                            if(request_type.equals("sent")){
                                current_state="request_sent";
                                cancelrequest.setVisibility(View.INVISIBLE);
                                sendrequestButton.setText("Cancel Friend Request");
                                cancelrequest.setEnabled(false);

                            }
                            else if(request_type.equals("recived")){
                                current_state="request_recived";
                                cancelrequest.setVisibility(View.VISIBLE);
                                cancelrequest.setEnabled(true);
                                sendrequestButton.setText("Accept Request");
                            }
                        }
                        else {
                            FriendRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(reciverUserId)){
                                                current_state="friends";
                                                cancelrequest.setVisibility(View.INVISIBLE);
                                                cancelrequest.setEnabled(false);
                                                sendrequestButton.setText("Unfriend");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void sendtofriendrequest() {
        FriendsrequestRef.child(senderUserId).child(reciverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsrequestRef.child(reciverUserId).child(senderUserId)
                                    .child("request_type").setValue("recived")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendrequestButton.setEnabled(true);
                                                current_state="request_send";
                                                sendrequestButton.setText("Cancel Request");
                                                cancelrequest.setVisibility(View.INVISIBLE);
                                                cancelrequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void castingelment() {
        current_state="not_friends";

        spprofile_username=(TextView) findViewById(R.id.personprofile_username);
        spprofile_college=(TextView) findViewById(R.id.personprofile_College);
        spprofile_DOB=(TextView) findViewById(R.id.personprofile_DOB);
        spprofile_status=(TextView) findViewById(R.id.personprofile_Status);
        spprofile_relationalStatus=(TextView) findViewById(R.id.personprofile_Relationship_Satus);
        spprofile_year=(TextView) findViewById(R.id.personprofile_Year);
        spprofile_gender=(TextView) findViewById(R.id.personprofile_Gender);
        spprofile_profileImage=(CircleImageView) findViewById(R.id.personprofile_profileImage);
    }
}
