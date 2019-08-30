package com.example.surya.knitconfession;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView friendsList;
    private FirebaseAuth mAuth;
    private DatabaseReference FriendsRef,UserRef;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mAuth=FirebaseAuth.getInstance();
        online_user_id=mAuth.getCurrentUser().getUid();
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsRef= FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        mToolbar=(Toolbar) findViewById(R.id.friends_app_barLayout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Friends");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent=new Intent(FriendsActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        friendsList=(RecyclerView) findViewById(R.id.recyclerforfriendsList);
        friendsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        friendsList.setLayoutManager(linearLayoutManager);
        showAllFriends();




    }

    private void showAllFriends() {
        FirebaseRecyclerAdapter<friends,FriendViewHolder> firebaseRecyclAdapter
                =new FirebaseRecyclerAdapter<friends, FriendViewHolder>(
                friends.class,
                R.layout.all_friends_layout,
                FriendViewHolder.class,
                FriendsRef
        ) {
            @Override
            protected void populateViewHolder(final FriendViewHolder viewHolder, friends model, int position) {
                final String usersId=getRef(position).getKey();
                viewHolder.setDate(model.getDate());
                UserRef.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            final  String username=dataSnapshot.child("username").getValue().toString();
                            final  String usercollege=dataSnapshot.child("usercollege").getValue().toString();
                            final  String userstatus=dataSnapshot.child("Status").getValue().toString();
                            final  String priofileImage=dataSnapshot.child("profileImage").getValue().toString();
                            viewHolder.setUsername(username);
                            viewHolder.setProfileImage(getApplicationContext(),priofileImage);
                            viewHolder.setStatus(userstatus);
                            viewHolder.setUsercollege(usercollege);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        friendsList.setAdapter(firebaseRecyclAdapter);

    }

    private static class FriendViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FriendViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setUsername(String username){
            TextView setfriendsUsername=(TextView) mView.findViewById(R.id.friends_username);
            setfriendsUsername.setText(username);
        }
        public void setProfileImage(Context ctx, String profileImage){
            CircleImageView friendscircleimageView=(CircleImageView) mView.findViewById(R.id.friends_profile);
            Picasso.with(ctx).load(profileImage).placeholder(R.drawable.us).into(friendscircleimageView);
        }
        public void setStatus(String status) {
            TextView setfriendsStatus = (TextView) mView.findViewById(R.id.friends_userstatus);
            setfriendsStatus.setText(status);
        }
        public void setUsercollege(String usercollege){
            TextView setfriendsCollege = (TextView) mView.findViewById(R.id.friends_userCollege);
            setfriendsCollege.setText(usercollege);
        }
        public void setDate(String date){
            TextView setfriendDate = (TextView) mView.findViewById(R.id.friendstime);
            setfriendDate.setText("Friends since"+ date);
        }
    }
}
