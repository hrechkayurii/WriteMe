package com.ua.yuriihrechka.writeme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class PhoneLoginActivity extends AppCompatActivity {


    private Button sendVerificationCodeBtn;
    private Button verifyBtn;
    private EditText inputPhoneNumer;
    private EditText inputVerificationCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        init();

    }

    private void init() {
        sendVerificationCodeBtn = (Button) findViewById(R.id.send_verification_button);
        verifyBtn = (Button) findViewById(R.id.verify_button);
        inputPhoneNumer = (EditText) findViewById(R.id.phone_number_input);
        inputVerificationCode = (EditText) findViewById(R.id.verification_code_input);

    }
}
