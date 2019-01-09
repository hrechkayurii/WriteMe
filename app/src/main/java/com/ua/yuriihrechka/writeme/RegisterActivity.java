package com.ua.yuriihrechka.writeme;

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

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText userEmail, userPassword;
    private TextView alreadyHaveAccountLink;

    private ProgressDialog loadingBar;

    // firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();


        InitializeFields();

        alreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });

    }

    private void CreateNewAccount() {

        String email = userEmail.getText().toString();
        String pass = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(pass)){
            Toast.makeText(getApplicationContext(), "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else {

            loadingBar.setTitle("Sing in");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        sendUserToLoginActivity();
                        Toast.makeText(RegisterActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    }else {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                    }

                    loadingBar.dismiss();
                }
            });
        }

    }

    private void InitializeFields() {

        registerButton = (Button)findViewById(R.id.register_button);
        userEmail = (EditText)findViewById(R.id.register_email);
        userPassword = (EditText)findViewById(R.id.register_password);
        alreadyHaveAccountLink = (TextView)findViewById(R.id.already_have_account_link);

        loadingBar = new ProgressDialog(this);
    }

    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

}
