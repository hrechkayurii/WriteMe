package com.ua.yuriihrechka.writeme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    private Button updateAccountButton;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settinds);

        initializeFields();
    }

    private void initializeFields() {
        updateAccountButton = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.profile_image);

    }
}
