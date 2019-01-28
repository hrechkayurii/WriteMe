package com.ua.yuriihrechka.writeme;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID;
    private CircleImageView userProfileImage;
    private TextView userProfileName;
    private TextView userProfileStatus;
    private Button sendMessageRequestButton, declineMessageRequestButton;

    private String currentState;
    private String senderUserID;

    private DatabaseReference dbUserRef, dbChatRequestRef, dbContactsRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat request");
        dbContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        auth = FirebaseAuth.getInstance();
        senderUserID = auth.getCurrentUser().getUid();


        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();



        init();
        
        retrieveUserInfo();
    }

    private void retrieveUserInfo() {

        dbUserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    manageChatRequest();

                }else {


                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();


                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    manageChatRequest();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init() {

         userProfileImage = (CircleImageView)findViewById(R.id.visit_profile_image);
         userProfileName = (TextView)findViewById(R.id.visit_user_name);
         userProfileStatus = (TextView)findViewById(R.id.visit_user_status);
         sendMessageRequestButton = (Button)findViewById(R.id.send_message_request_button);
         declineMessageRequestButton = (Button)findViewById(R.id.decline_message_request_button);

         currentState = "new";

    }

    private void manageChatRequest(){

        dbChatRequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(receiverUserID)){
                            String request_type = dataSnapshot.child(receiverUserID).child("request_type")
                                    .getValue().toString();

                            if (request_type.equals("sent")){
                                currentState = "request_sent";
                                sendMessageRequestButton.setText("Cancel chat request");
                            }else if (request_type.equals("received")){
                                currentState = "request_received";
                                sendMessageRequestButton.setText("Accept chat request");
                                declineMessageRequestButton.setVisibility(View.VISIBLE);
                                declineMessageRequestButton.setEnabled(true);

                                declineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelChatRequest();
                                    }
                                });

                            }
                        }else {
                            dbContactsRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(receiverUserID)){
                                                currentState = "friends";
                                                sendMessageRequestButton.setText("Remove friend");

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        if (!senderUserID.equals(receiverUserID)){

            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessageRequestButton.setEnabled(false);
                    if (currentState.equals("new")){
                        sendChatRequest();
                    }

                    if (currentState.equals("request_sent")){
                        cancelChatRequest();
                    }

                    if (currentState.equals("request_sent")){
                        acceptChatRequest();
                    }

                    if (currentState.equals("friends")){
                        removeSpecificContact();
                    }
                }
            });

        }else {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    private void removeSpecificContact() {

        dbContactsRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            dbContactsRef.child(receiverUserID)
                                    .child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                sendMessageRequestButton.setEnabled(true);
                                                currentState = "new";
                                                sendMessageRequestButton.setText("Send message");

                                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                declineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }

                    }
                });

    }

    private void acceptChatRequest() {

        dbContactsRef.child(receiverUserID).child(senderUserID)
                .child("Contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            dbChatRequestRef.child(senderUserID).child(receiverUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                dbChatRequestRef.child(receiverUserID).child(senderUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {

                                                                    sendMessageRequestButton.setEnabled(true);
                                                                    currentState = "friends";
                                                                    sendMessageRequestButton.setTag("Remove friend");
                                                                    declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                    sendMessageRequestButton.setEnabled(false);

                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                        }

                    }


                });
    }

    private void cancelChatRequest() {

        dbChatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            dbChatRequestRef.child(receiverUserID)
                                    .child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                sendMessageRequestButton.setEnabled(true);
                                                currentState = "new";
                                                sendMessageRequestButton.setText("Send message");

                                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                declineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }

                    }
                });

    }

    private void sendChatRequest() {
        dbChatRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dbChatRequestRef.child(receiverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendMessageRequestButton.setEnabled(true);
                                                currentState = "request_sent";
                                                sendMessageRequestButton.setText("Cancel chat request");
                                            }
                                        }
                                    });
                        }
                    }
                });

    }
}
