package com.example.surya.knitconfession;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.LineNumberReader;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef,postsRef,likeRef;
    private CircleImageView profilenavImage;
    private TextView profileNavuserName;
    private ImageButton postingtheapp,searchButton;
    String currentUserId;
    boolean likecheker=false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Search Button
        searchButton=(ImageButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToSearchActivity();
            }
        });
        //firebase
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        firebaseAuth =FirebaseAuth.getInstance();
        currentUserId=firebaseAuth.getCurrentUser().getUid();
        //NAvigationView
        drawerLayout =(DrawerLayout) findViewById(R.id.drawer_layout);

        //RecyclerView
        recyclerView =(RecyclerView) findViewById(R.id.post_item);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutmanager=new LinearLayoutManager(this);
        linearLayoutmanager.setReverseLayout(true);
        linearLayoutmanager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutmanager);
        disPlayalluserpost();

        //NavHeader
        navigationView=(NavigationView) findViewById(R.id.navigaion_view);
        View navView=navigationView.inflateHeaderView(R.layout.header_item);
        profilenavImage=(CircleImageView) navView.findViewById(R.id.navProfileImage);
        profileNavuserName=(TextView) navView.findViewById(R.id.userName_header);
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("username")) {
                        String Userfullname = dataSnapshot.child("username").getValue().toString();
                        profileNavuserName.setText(Userfullname);
                    }
                    if (dataSnapshot.hasChild("profileImage")) {
                        String userImage = dataSnapshot.child("profileImage").getValue().toString();

                        Picasso.with(MainActivity.this).load(userImage).placeholder(R.drawable.us).into(profilenavImage);
                    }
                }
                else{
                    Toast.makeText(MainActivity.this,"Profile name does not Exists... ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Posting post
        postingtheapp=(ImageButton) findViewById(R.id.postingButton);
        postingtheapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTopostActivity();
            }
        });


        mToolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Home");
        actionBarDrawerToggle =new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }

        });

    }

    private void sendToSearchActivity() {
        Intent searchIntent=new Intent(MainActivity.this,SearchActivity.class);
        startActivity(searchIntent);
        finish();
    }

    private void disPlayalluserpost() {
        Query sortPostInDescending=postsRef.orderByChild("postcount");
        FirebaseRecyclerAdapter<posts,PostViewHolder> firebaseRecycleAdapter=
                new FirebaseRecyclerAdapter<posts, PostViewHolder>(
                        posts.class,
                        R.layout.all_post,
                        PostViewHolder.class,
                        sortPostInDescending
                ) {
                    @Override
                    protected void populateViewHolder(PostViewHolder viewHolder, posts model, int position)
                    {
                        final String Postkey=getRef(position).getKey();
                        viewHolder.setFullname(model.getFullname());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setPostimage(getApplicationContext(),model.getPostimage());
                        viewHolder.setProfileimage(getApplicationContext(),model.getProfileimage());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent clickPostIntent=new Intent(MainActivity.this,ClickPostActivity.class);
                                clickPostIntent.putExtra("postkey",Postkey);
                                startActivity(clickPostIntent);

                            }
                        });
                        viewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent CommentIntent=new Intent(MainActivity.this,CommentActivity.class);
                                CommentIntent.putExtra("postkey",Postkey);
                                startActivity(CommentIntent);
                            }
                        });
                        viewHolder.setLikeButtonStatus(Postkey);
                        viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                likecheker=true;
                                likeRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                       if(likecheker==true){
                                           if(dataSnapshot.child(Postkey).hasChild(currentUserId)){
                                               likeRef.child(Postkey).child(currentUserId).removeValue();
                                               likecheker=false;


                                           }
                                           else {
                                               likeRef.child(Postkey).child(currentUserId).setValue(true);
                                               likecheker=false;

                                           }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                    }
                };
        recyclerView.setAdapter(firebaseRecycleAdapter);

    }
    public static class PostViewHolder extends RecyclerView.ViewHolder
    {

        View mView;

        ImageButton likeButton,shareButton,commentButton;
        TextView likecount,commentcount;
        int countLike;
        String currentUserId;
        DatabaseReference likeRef;
        public PostViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            likeButton=(ImageButton) mView.findViewById(R.id.likeButon);
            commentButton=(ImageButton) mView.findViewById(R.id.commentButon);
            shareButton=(ImageButton) mView.findViewById(R.id.shareButon);
            likecount=(TextView) mView.findViewById(R.id.likecounttext);

            likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();



        }
        public void setLikeButtonStatus(final String Postkey){
            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(Postkey).hasChild(currentUserId)){
                        countLike=(int) dataSnapshot.child(Postkey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.heartred);
                        likecount.setText(Integer.toString(countLike));
                    }
                    else {
                        countLike=(int) dataSnapshot.child(Postkey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.heartblack);
                        likecount.setText(Integer.toString(countLike));

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void setFullname(String fullname){
            TextView userfullname=(TextView) mView.findViewById(R.id.post_user_name);
            userfullname.setText(fullname);
        }
        public void setProfileimage(Context ctx,String profileimage){
            CircleImageView postprofileImage=(CircleImageView) mView.findViewById(R.id.post_profile_pic);
            Picasso.with(ctx).load(profileimage).into(postprofileImage);
        }
        public void setTime(String time){
            TextView posttime=(TextView) mView.findViewById(R.id.post_timing);
            posttime.setText(time);
        }
        public void setDate(String date){
            TextView postdate=(TextView) mView.findViewById(R.id.post_date);
            postdate.setText(date);
        }
        public void setPostimage(Context ctx,String postimage){
            ImageView postimagebyuser=(ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(postimage).into(postimagebyuser);

        }
        public void setDescription(String description){
            TextView postdescription =(TextView) mView.findViewById(R.id.post_description);
            postdescription.setText(description);

        }
    }
    private void sendTopostActivity() {
        Intent postactivityIntent=new Intent(MainActivity.this,PostActivity.class);
        postactivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(postactivityIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_home:
                SendTomainActivity();
                 break;
            case R.id.nav_profile:
                sendToProfileActivity();
                break;
            case R.id.nav_setting:
                sendTosettingActivity();
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                mainSendUserToLoginActivity();
                break;
            case R.id.nav_friends:
                sendToFriendsActivity();
        }
    }

    private void sendToFriendsActivity() {
        Intent profileActivity=new Intent(MainActivity.this,FriendsActivity.class);
        profileActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileActivity);
        finish();
    }

    private void sendToProfileActivity() {
        Intent profileActivity=new Intent(MainActivity.this,ProfileActivity.class);
        profileActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileActivity);
        finish();

    }

    private void SendTomainActivity() {
        Intent selfIntent=new Intent(MainActivity.this,MainActivity.class);
        startActivity(selfIntent);
        finish();
    }

    private void sendTosettingActivity() {
        Intent settingIntent=new Intent(MainActivity.this,SettingActivity.class);
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser =firebaseAuth.getCurrentUser();
        if(currentUser==null){
            mainSendUserToLoginActivity();
        }
        else{
            checkUserExist();
        }
    }


    private void checkUserExist() {
        final String cureent_User_id=firebaseAuth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(cureent_User_id)){
                    mainsendToSetUpActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void mainsendToSetUpActivity() {
        Intent setUptIntent =new Intent(MainActivity.this,setUpactivity.class);
        setUptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setUptIntent);
        finish();
    }

    private void mainSendUserToLoginActivity() {
        Intent loginIntent =new Intent(MainActivity.this,Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }
}
