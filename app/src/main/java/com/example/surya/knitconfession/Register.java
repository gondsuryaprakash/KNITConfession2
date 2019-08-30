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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    private EditText RegiseruserEmail,RegisterUserPassword,RegisterUserConfirmPassword;
    private Button userCreateButton;
    private ProgressDialog loadProgressDialoge;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loadProgressDialoge=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        RegisterUserPassword=(EditText) findViewById(R.id.password_register);
        RegisterUserConfirmPassword=(EditText) findViewById(R.id.confirm_password_register);
        RegiseruserEmail=(EditText) findViewById(R.id.email_register);
        userCreateButton =(Button) findViewById(R.id.button_register);
        userCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatNewAccount();
            }
        });
    }

    private void creatNewAccount() {
        String email=RegiseruserEmail.getText().toString();
        String userPassword=RegisterUserPassword.getText().toString();
        String userConfirmPassword=RegisterUserConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(Register.this,"Please Enter the Email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userPassword)){
            Toast.makeText(Register.this,"Please Enter the Password",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userConfirmPassword)){
            Toast.makeText(Register.this,"Please Enter the Confirm Password",Toast.LENGTH_SHORT).show();
        }
        else if(!userPassword.equals(userConfirmPassword)){
            Toast.makeText(Register.this,"Your password is not match with confirm password",Toast.LENGTH_SHORT).show();
        }
        else {
            loadProgressDialoge.setTitle("Creating new Account");
            loadProgressDialoge.setMessage("Please wait while we creating your Account... ");
            loadProgressDialoge.show();
            loadProgressDialoge.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(email,userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendTosetUpactivity();
                                Toast.makeText(Register.this,"Your Account is created",Toast.LENGTH_SHORT).show();
                                loadProgressDialoge.dismiss();
                            }
                            else{
                                String message=task.getException().getMessage();
                                Toast.makeText(Register.this,"Error occured" + message ,Toast.LENGTH_SHORT).show();
                                loadProgressDialoge.dismiss();
                            }
                        }
                    });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            sendUserTomainActivity();
        }
    }

    private void sendUserTomainActivity() {
        Intent mainIntent =new Intent(Register.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendTosetUpactivity() {
        Intent setUpIntent =new Intent(Register.this,setUpactivity.class);
        setUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setUpIntent);
        finish();
    }


}
