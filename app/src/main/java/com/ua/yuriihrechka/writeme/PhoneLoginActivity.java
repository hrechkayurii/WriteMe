package com.ua.yuriihrechka.writeme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {


    private Button sendVerificationCodeBtn;
    private Button verifyBtn;
    private EditText inputPhoneNumber;
    private EditText inputVerificationCode;
    private ProgressDialog mProgressDialog;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
   // private PhoneAuthCredential credential;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();

        init();

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendVerificationCodeBtn.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);

                String verificationCode = inputVerificationCode.getText().toString();
                if (TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(PhoneLoginActivity.this, "Please write code", Toast.LENGTH_LONG).show();
                }else {

                    mProgressDialog.setTitle("Code verification");
                    mProgressDialog.setMessage("please wait verification");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        sendVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = inputPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(PhoneLoginActivity.this, "Enter phone number", Toast.LENGTH_LONG).show();
                }else {

                    mProgressDialog.setTitle("Phone verification");
                    mProgressDialog.setMessage("please wait verification");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                mProgressDialog.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Error: "+e, Toast.LENGTH_LONG).show();

                sendVerificationCodeBtn.setVisibility(View.VISIBLE);
                inputPhoneNumber.setVisibility(View.VISIBLE);

                verifyBtn.setVisibility(View.INVISIBLE);
                inputVerificationCode.setVisibility(View.INVISIBLE);

            }


            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                mProgressDialog.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "please check and verify...", Toast.LENGTH_LONG).show();

                sendVerificationCodeBtn.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);

                verifyBtn.setVisibility(View.VISIBLE);
                inputVerificationCode.setVisibility(View.VISIBLE);
            }

        };

    }

    private void init() {
        sendVerificationCodeBtn = (Button) findViewById(R.id.send_verification_button);
        verifyBtn = (Button) findViewById(R.id.verify_button);
        inputPhoneNumber = (EditText) findViewById(R.id.phone_number_input);
        inputVerificationCode = (EditText) findViewById(R.id.verification_code_input);

        mProgressDialog = new ProgressDialog(this);


    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mProgressDialog.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Successful", Toast.LENGTH_LONG).show();
                            sendUserToMainActivity();

                        } else {

                            String message = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error: "+message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendUserToMainActivity() {

        Intent intentMainActivity = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(intentMainActivity);
        finish();
    }
}
