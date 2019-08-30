package com.example.surya.knitconfession;

import android.content.Intent;
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

public class ResetPassActivity extends AppCompatActivity {
    private EditText email;
    private Button sendEmailButton;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        mToolbar=(Toolbar) findViewById(R.id.resetactivit_app_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent=new Intent(ResetPassActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        mAuth =FirebaseAuth.getInstance();
        email=(EditText) findViewById(R.id.resetemail_login);
        sendEmailButton=(Button) findViewById(R.id.resetbutton_login);
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailaddress=email.getText().toString();
                if(TextUtils.isEmpty(emailaddress)){
                    Toast.makeText(ResetPassActivity.this,"Please Enter the valid email...",Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(emailaddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPassActivity.this,"Please check your email...",Toast.LENGTH_SHORT).show();
                                sendToLoginActivity();
                            }
                            else
                            {
                                Toast.makeText(ResetPassActivity.this,"Error Occured...",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });
    }

    private void sendToLoginActivity() {
        Intent sendTologinIntent=new Intent(ResetPassActivity.this,Login.class);
        sendTologinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendTologinIntent);
        finish();                      
    }

}
