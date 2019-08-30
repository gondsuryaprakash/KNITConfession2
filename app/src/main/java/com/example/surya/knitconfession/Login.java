package com.example.surya.knitconfession;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private Button loginButton;
    private EditText email_login,password_login;
    private TextView needNewAccountLink,forgotpass;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        mAuth=FirebaseAuth.getInstance();
        loadingbar =new ProgressDialog(this);
        needNewAccountLink=(TextView) findViewById(R.id.suggestionfor_signup);
        forgotpass=(TextView) findViewById(R.id.forgotpass_login);
        loginButton =(Button) findViewById(R.id.button_login);
        email_login =(EditText) findViewById(R.id.email_login);
        password_login=(EditText) findViewById(R.id.password_login);
        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowingtologin();
            }
        });
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToResetPasswordActivity();
            }
        });




    }

    private void sendToResetPasswordActivity() {
        Intent resetIntent=new Intent(Login.this,ResetPassActivity.class);
        resetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(resetIntent);
        finish();
    }

    private void allowingtologin() {
        String userpassword=password_login.getText().toString();
        String userEmail=email_login.getText().toString();
        if(TextUtils.isEmpty(userpassword)){
            Toast.makeText(Login.this,"Please Enter the Password...",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(Login.this,"Please Enter the Email...",Toast.LENGTH_SHORT).show();

        }
        else {
            loadingbar.setTitle("Loginng");
            loadingbar.setMessage("Loading to login please wait....");
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(userEmail,userpassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loginsendToMainActivity();
                                loadingbar.dismiss();
                                Toast.makeText(Login.this,"You are logged in...",Toast.LENGTH_SHORT).show();
                            }
                            else {

                                String message=task.getException().getMessage();
                                Toast.makeText(Login.this,"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });
        }

    }

    private void loginsendToMainActivity() {
        Intent mainIntent=new Intent(Login.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();

        if (currentUser!=null){
            loginsendToMainActivity();
        }
    }


    private void sendUserToRegisterActivity() {
        Intent registerIntent=new Intent(Login.this,Register.class);
        startActivity(registerIntent);
        finish();
    }
}
