package com.ua.yuriihrechka.writeme;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment {

    private View chatView;
    private RecyclerView chatList;

    private DatabaseReference dbRef, dbUserRef;
    private FirebaseAuth auth;

    private String currentUserID;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatView = inflater.inflate(R.layout.fragment_chats, container, false);

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        init();

        return chatView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(dbRef, Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts, ChatViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contacts model) {

                        final String userID = getRef(position).getKey();

                        dbUserRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild("image")){

                                    final String retImage = dataSnapshot.child("image").getValue().toString();
                                    Picasso.get().load(retImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                }

                                final String retName = dataSnapshot.child("name").getValue().toString();
                                final String retStatus = dataSnapshot.child("status").getValue().toString();

                                holder.userName.setText(retName);
                                holder.userStatus.setText(retStatus);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new ChatViewHolder(v);

                    }
                };

        chatList.setAdapter(adapter);
        adapter.startListening();

    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView userName, userStatus;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = (CircleImageView)itemView.findViewById(R.id.users_profile_image);
            userName = (TextView)itemView.findViewById(R.id.user_profile_name);
            userStatus = (TextView)itemView.findViewById(R.id.user_status);
        }
    }

    private void init() {

        chatList = (RecyclerView)chatView.findViewById(R.id.chat_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
