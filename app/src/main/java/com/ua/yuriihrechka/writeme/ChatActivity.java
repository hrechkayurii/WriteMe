package com.ua.yuriihrechka.writeme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {

    private String messageReceivedID, messageReceiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageReceivedID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();

        Toast.makeText(this, messageReceivedID, Toast.LENGTH_LONG).show();
        Toast.makeText(this, messageReceiverName, Toast.LENGTH_LONG).show();
    }
}
