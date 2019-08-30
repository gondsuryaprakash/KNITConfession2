package com.example.surya.knitconfession;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private ImageButton messagePicbutton,MessageSendButton;
    private EditText messageContent;
    private String messageReciverId,messagesSenderId,saveCurrentDate,saveCurrentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef,UserRef;
    private Toolbar mToolbar;
    private TextView messageUserName;
    private CircleImageView messageProfileImage;
    private RecyclerView messageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        messageReciverId=getIntent().getExtras().get("personkey").toString();
        mAuth=FirebaseAuth.getInstance();
        messagesSenderId=mAuth.getCurrentUser().getUid();
        messageList=(RecyclerView) findViewById(R.id.Recuclerformessage);
        innitializeValue();
        RootRef= FirebaseDatabase.getInstance().getReference();
        displayReciverInfo();
        MessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendmessage();
            }
        });
    }

    private void sendmessage() {
        String messageText=messageContent.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(MessageActivity.this,"Empty message",Toast.LENGTH_SHORT).show();
        }
        else {
            String message_sender_ref="Message/" + messagesSenderId + "/" + messageReciverId;
            String message_reciver_ref="Message/" + messageReciverId + "/" + messagesSenderId;
            DatabaseReference user_message_key=RootRef.child("Message").child(messagesSenderId)
                    .child(messageReciverId).push();
            String message_puch_id=user_message_key.getKey();
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            saveCurrentTime = currentTime.format(calFordDate.getTime());
            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("date",saveCurrentDate);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messagesSenderId);
            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(message_sender_ref + "/" + message_puch_id,messageTextBody);
            messageBodyDetails.put(message_reciver_ref + "/" + message_puch_id,messageTextBody);
            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(MessageActivity.this,"Message sent",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MessageActivity.this,"Message not sent",Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }
    }

    private void displayReciverInfo() {
        RootRef.child("Users").child(messageReciverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    final String username=dataSnapshot.child("username").getValue().toString();
                    final String profileimage=dataSnapshot.child("profileImage").getValue().toString();
                    Picasso.with(MessageActivity.this).load(profileimage).placeholder(R.drawable.us).into(messageProfileImage);
                    messageUserName.setText(username);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void innitializeValue() {
        messagePicbutton=(ImageButton) findViewById(R.id.imagesendingbutton);
        MessageSendButton=(ImageButton) findViewById(R.id.messagesendbutton);
        messageContent=(EditText) findViewById(R.id.messagesendingBOX);
        mToolbar=(Toolbar) findViewById(R.id.messageAppBar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Message");
        messageUserName=(TextView) findViewById(R.id.messageusername);
        messageProfileImage=(CircleImageView) findViewById(R.id.message_profile);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_View=layoutInflater.inflate(R.layout.chat_custom_layout,null);
        actionBar.setCustomView(action_bar_View);

    }
}
