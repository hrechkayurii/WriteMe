package com.ua.yuriihrechka.writeme;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

//20190129
public class RequestFragment extends Fragment {

    private View requestFragmentView;
    private RecyclerView requestList;

    private DatabaseReference dbChatRequestRef, dbUserRef, dbContactsRef;
    private FirebaseAuth auth;

    private String currentUserID;



    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //1
        requestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);

        dbChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat request");
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        requestList = (RecyclerView)requestFragmentView.findViewById(R.id.chat_request_list);
        requestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //4
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(dbChatRequestRef.child(currentUserID), Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, RequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contacts model) {

                        //holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                        //holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(position).getKey();

                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){

                                    String type = dataSnapshot.getValue().toString();

                                    if (type.equals("received")){
                                        dbUserRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")) {

                                                    final String requestUserImage = dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(requestUserImage).placeholder(R.drawable.profile_image).into(holder.userImage);

                                                }

                                                final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                final String requestUserStatus = dataSnapshot.child("status").getValue().toString();


                                                holder.userName.setText(requestUserName);
                                                holder.userStatus.setText(requestUserStatus);

                                                holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                                                holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);

                                                holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        dbContactsRef.child(currentUserID).child(list_user_id)
                                                                .child("Contacts")
                                                                .setValue("saved")
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {

                                                                            dbContactsRef.child(list_user_id).child(currentUserID)
                                                                                    .child("Contacts")
                                                                                    .setValue("saved")
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {

                                                                                                dbChatRequestRef.child(currentUserID).child(list_user_id)
                                                                                                        .removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                if (task.isSuccessful()) {

                                                                                                                    dbChatRequestRef.child(list_user_id).child(currentUserID)
                                                                                                                            .removeValue()
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        Toast.makeText(getContext(), "add", Toast.LENGTH_LONG).show();

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
                                                                    }
                                                                });

                                                    }
                                                });

                                                holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        dbChatRequestRef.child(currentUserID).child(list_user_id)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()){

                                                                            dbChatRequestRef.child(list_user_id).child(currentUserID)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                            if (task.isSuccessful()){
                                                                                                Toast.makeText(getContext(), "deleted", Toast.LENGTH_LONG).show();

                                                                                            }
                                                                                        }
                                                                                    });

                                                                        }
                                                                    }
                                                                });

                                                    }
                                                });



                                                /*holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        CharSequence options[] = new CharSequence[]{
                                                            "Accept", "Cancel"
                                                        };

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle(requestUserName + " Chat request");
                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                if (which == 0){
                                                                    dbContactsRef.child(currentUserID).child(list_user_id)
                                                                            .child("Contacts")
                                                                            .setValue("saved")
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                     if (task.isSuccessful()){

                                                                                         dbContactsRef.child(list_user_id).child(currentUserID)
                                                                                                 .child("Contacts")
                                                                                                 .setValue("saved")
                                                                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                     @Override
                                                                                                     public void onComplete(@NonNull Task<Void> task) {
                                                                                                         if (task.isSuccessful()){

                                                                                                             dbContactsRef.child(currentUserID).child(list_user_id)
                                                                                                                     .removeValue()
                                                                                                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                         @Override
                                                                                                                         public void onComplete(@NonNull Task<Void> task) {

                                                                                                                             if (task.isSuccessful()){

                                                                                                                                 dbContactsRef.child(list_user_id).child(currentUserID)
                                                                                                                                         .removeValue()
                                                                                                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                             @Override
                                                                                                                                             public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                                 if (task.isSuccessful()){
                                                                                                                                                     Toast.makeText(getContext(), "add", Toast.LENGTH_LONG).show();

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
                                                                                }
                                                                            });
                                                                }

                                                                if (which == 1){

                                                                    dbChatRequestRef.child(currentUserID).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if (task.isSuccessful()){

                                                                                        dbChatRequestRef.child(list_user_id).child(currentUserID)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        if (task.isSuccessful()){
                                                                                                            Toast.makeText(getContext(), "deleted", Toast.LENGTH_LONG).show();

                                                                                                        }
                                                                                                    }
                                                                                                });

                                                                                    }
                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });

                                                        builder.show();

                                                    }
                                                });*/

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        //2
                        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        RequestViewHolder holder = new RequestViewHolder(v);
                        return  holder;
                    }
                };


        requestList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{



        TextView userName;
        TextView userStatus;
        CircleImageView userImage;
        Button acceptBtn;
        Button cancelBtn;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            //3
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            userImage = itemView.findViewById(R.id.users_profile_image);

            acceptBtn = itemView.findViewById(R.id.request_accept_button);
            cancelBtn = itemView.findViewById(R.id.request_cancel_button);

        }

    }
}
