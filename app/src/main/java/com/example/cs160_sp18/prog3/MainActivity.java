package com.example.cs160_sp18.prog3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName, passWord;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    String email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        userName = findViewById(R.id.username);
        passWord = findViewById(R.id.password);


        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        String user = userName.getText().toString();
        String pass = passWord.getText().toString();
        if(i == R.id.register) {
            registerUser(user, pass);

        } else if(i == R.id.login) {
            login(user, pass);
        }
    }


    private void login(String username, String password) {
        if (!validateForm()){
            return;
        }

        email = username;


        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loadingBar.setTitle("Welcome, " + email);
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();
                    Intent first = new Intent(MainActivity.this, SecondActivity.class);
                    first.putExtra("username", email);
                    startActivity(first);


                } else {
                    String msg = task.getException().toString();
                    Toast.makeText(MainActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(String un, String pw) {
        if(!validateForm()) {
            return;
        }
        loadingBar.setTitle("Creating New Account");
        loadingBar.setMessage("Please wait, while we are creating your account.. ");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        email = un;
        pass = pw;

        mAuth.createUserWithEmailAndPassword(un, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            loadingBar.setTitle("Account Created Successfully");
                            loadingBar.setMessage("Now logging into your account");
                            loadingBar.setCanceledOnTouchOutside(true);
                            loadingBar.show();
                            login(email, pass);
                            Intent first = new Intent(MainActivity.this, SecondActivity.class);
                            first.putExtra("username", email);
                            startActivity(first);

                        } else {
                            String msg = task.getException().toString();
                            Toast.makeText(MainActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String user = userName.getText().toString();
        String pass = passWord.getText().toString();

        if (TextUtils.isEmpty(user)) {
            userName.setError("Required.");
            valid = false;
        } else {
            userName.setError(null);
        }


        if (TextUtils.isEmpty(pass)) {
            passWord.setError("Required.");
            valid = false;
        } else {
            passWord.setError(null);
        }
        return valid;
    }
    }
