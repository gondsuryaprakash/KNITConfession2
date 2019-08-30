package com.example.surya.knitconfession;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {
    private Button click_post_edit_Button;
    private Button click_post_delete_Button;
    private TextView click_post_description_text;
    private ImageView click_post_image;
    private String PostKey, Current_user_id,dataBaseUserId, Description,PostImage;
    private DatabaseReference ClickPostRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);
        //FirebAseAuth
        mAuth=FirebaseAuth.getInstance();
        Current_user_id = mAuth.getCurrentUser().getUid();
        //PostKey
        PostKey=getIntent().getExtras().get("postkey").toString();

        //PostDescription
        click_post_description_text=(TextView) findViewById(R.id.click_post_description);
        //postImage
        click_post_image=(ImageView) findViewById(R.id.click_post_image);
        //DatabaseRefrence
        ClickPostRef= FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                   Description=dataSnapshot.child("description").getValue().toString();
                   PostImage=dataSnapshot.child("postimage").getValue().toString();
                   dataBaseUserId=dataSnapshot.child("uid").getValue().toString();
                   click_post_description_text.setText(Description);
                   Picasso.with(ClickPostActivity.this).load(PostImage).into(click_post_image);
                   if(Current_user_id.equals(dataBaseUserId)) {
                       click_post_edit_Button.setVisibility(View.VISIBLE);
                       click_post_delete_Button.setVisibility(View.VISIBLE);
                   }
                   click_post_edit_Button.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           EditCurrentPost(Description);
                       }
                   });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //EditButton
        click_post_edit_Button=(Button) findViewById(R.id.click_post_editButton);
        click_post_edit_Button.setVisibility(View.INVISIBLE);


        //DeletButton
        click_post_delete_Button=(Button) findViewById(R.id.click_post_deleteButton);
        click_post_delete_Button.setVisibility(View.INVISIBLE);
        click_post_delete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteCurrentPost();
            }
        });
    }

    private void EditCurrentPost(String Description) {

        AlertDialog.Builder builder=new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit post");
        final EditText inputField=new EditText(ClickPostActivity.this);
        inputField.setText(Description);
        builder.setView(inputField);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this,"Post updated successfully",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        Dialog dialog=builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorWhite);

    }

    private void DeleteCurrentPost() {
        ClickPostRef.removeValue();
        SendTheUserToMainActivity();
    }

    private void SendTheUserToMainActivity() {

        Intent mainIntent=new Intent(ClickPostActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
