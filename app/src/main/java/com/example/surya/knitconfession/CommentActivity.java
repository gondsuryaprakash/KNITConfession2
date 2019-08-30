package com.example.surya.knitconfession;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {
    private EditText commentedit;
    private Button commentButton;
    private RecyclerView commentList;
    private String Post_key,currentUserId;
    private DatabaseReference UserRef,PostRef;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mToolbar=(Toolbar) findViewById(R.id.appBar_profileActivity);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendTomainActivity=new Intent(CommentActivity.this,MainActivity.class);
                startActivity(sendTomainActivity);
                finish();
            }
        });
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        Post_key=getIntent().getExtras().get("postkey").toString();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef= FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_key).child("Comments");
        commentButton=(Button) findViewById(R.id.comments_commentButton);
        commentedit=(EditText) findViewById(R.id.comments_CommentsBox);
        //recyclerview
        commentList =(RecyclerView) findViewById(R.id.commentRecycler);
        commentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CommentActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentList.setLayoutManager(linearLayoutManager);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String userName=dataSnapshot.child("username").getValue().toString();
                            validatecomment(userName);
                            commentedit.setText(" ");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<commentclass,CommentViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<commentclass, CommentViewHolder>(
                commentclass.class,
                R.layout.all_coomens_layout,
                CommentViewHolder.class,
                PostRef ) {
            @Override
            protected void populateViewHolder(CommentViewHolder viewHolder, commentclass model, int position) {
                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());

            }
        };
        commentList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public CommentViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setComment(String comment){
            TextView commentbyuser=(TextView) mView.findViewById(R.id.commentText);
            commentbyuser.setText(comment);
        }
        public void setDate(String date){
            TextView commentdate=(TextView) mView.findViewById(R.id.commentDate);
            commentdate.setText(date);

        }
        public void setUsername(String username){
            TextView commentuser=(TextView) mView.findViewById(R.id.commentusername);
            commentuser.setText(username);

        }
        public void setTime(String time){
            TextView commentbyuser=(TextView) mView.findViewById(R.id.commentTime);
            commentbyuser.setText(time);

        }
    }

    private void validatecomment(String userName) {
        String commentText=commentedit.getText().toString();
        if(TextUtils.isEmpty(commentText)){
            Toast.makeText(CommentActivity.this,"Please Write the comment",Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());
            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());
            final String RandomKey=currentUserId + saveCurrentDate + saveCurrentTime;
            HashMap commentMap=new HashMap();
            commentMap.put("uid",currentUserId);
            commentMap.put("comment",commentText);
            commentMap.put("date",saveCurrentDate);
            commentMap.put("time",saveCurrentTime);
            commentMap.put("username",userName);
            PostRef.child(RandomKey).updateChildren(commentMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(CommentActivity.this,"Comment Successful...",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(CommentActivity.this,"Some error occured..",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });



        }
    }
}
