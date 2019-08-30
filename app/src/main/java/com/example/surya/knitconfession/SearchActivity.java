package com.example.surya.knitconfession;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference SearchRef;
    private EditText searchEditBox;
    private ImageButton searchresultButton;
    private RecyclerView userList;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchEditBox=(EditText) findViewById(R.id.search_editbox);
        searchresultButton=(ImageButton) findViewById(R.id.searchbuttonforreult);
        mAuth=FirebaseAuth.getInstance();
        SearchRef= FirebaseDatabase.getInstance().getReference().child("Users");
        userList=(RecyclerView) findViewById(R.id.search_recycleView);
        userList.setHasFixedSize(true);
        userList.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        mToolbar=(Toolbar) findViewById(R.id.search_app_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Search People");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendTonewmainIntent=new Intent(SearchActivity.this,MainActivity.class);
                startActivity(sendTonewmainIntent);
                finish();
            }
        });


        searchresultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchtext=searchEditBox.getText().toString();
                functiontogivesearchserault(searchtext);
            }
        });



    }

    private void functiontogivesearchserault(String searchtext) {
        Toast.makeText(SearchActivity.this,"Searching..",Toast.LENGTH_SHORT).show();
        Query searchPeopleandFriendsQuery=SearchRef.orderByChild("username").startAt(searchtext).endAt(searchtext +"\uf8ff");

        FirebaseRecyclerAdapter<search,FinfFriendViewHolder> firebaseRecyclrAdapter
                =new FirebaseRecyclerAdapter<search, FinfFriendViewHolder>(
                search.class,
                R.layout.all_user_by_search,
                FinfFriendViewHolder.class,
                searchPeopleandFriendsQuery
        ) {
            @Override
            protected void populateViewHolder(FinfFriendViewHolder viewHolder, search model, int position) {
                final String personPostkey=getRef(position).getKey();
                viewHolder.setUsername(model.getUsername());
                viewHolder.setUsercollege(model.getUsercollege());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setProfileImage(getApplicationContext(),model.getProfileImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence option[]=new CharSequence[]{
                                "Profile"
                                ,
                                "Send Message"

                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(SearchActivity.this);
                        builder.setTitle("Select Options");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    Intent sendToProfileIntent=new Intent(SearchActivity.this,ProfileActivity.class);
                                    startActivity(sendToProfileIntent);
                                    sendToProfileIntent.putExtra("personkey",personPostkey);
                                    finish();
                                }
                                if(i==1){

                                    Intent sendToMessageIntent=new Intent(SearchActivity.this,MessageActivity.class);
                                    startActivity(sendToMessageIntent);
                                    sendToMessageIntent.putExtra("personkey",personPostkey);
                                    finish();

                                }

                            }
                        });
                        builder.show();



                    }
                });


            }
        };
        userList.setAdapter(firebaseRecyclrAdapter);
    }
    public static class FinfFriendViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FinfFriendViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setUsername(String username){
            TextView setUsername=(TextView) mView.findViewById(R.id.search_resul_username);
            setUsername.setText(username);

        }
        public void setProfileImage(Context ctx, String profileImage){
            CircleImageView circleimageView=(CircleImageView) mView.findViewById(R.id.search_result_profile);
            Picasso.with(ctx).load(profileImage).placeholder(R.drawable.us).into(circleimageView);
        }
        public void setStatus(String status) {
            TextView setStatus = (TextView) mView.findViewById(R.id.search_resul_userstatus);
            setStatus.setText(status);
        }
        public void setUsercollege(String usercollege){
            TextView setCollege = (TextView) mView.findViewById(R.id.search_resul_userCollege);
            setCollege.setText(usercollege);

        }



    }
}
