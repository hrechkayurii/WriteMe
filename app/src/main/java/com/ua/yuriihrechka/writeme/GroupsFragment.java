package com.ua.yuriihrechka.writeme;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.telephony.mbms.MbmsErrors;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GroupsFragment extends Fragment {


    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<String> listOfGroups = new ArrayList<>();

    private DatabaseReference mGroupRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        mGroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        InitializeFields();
        retrieveAndDisplayGroup();

        return groupFragmentView;
    }

    private void retrieveAndDisplayGroup() {

        mGroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String>set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){

                    set.add(((DataSnapshot)iterator.next()).getKey());

                }

                listOfGroups.clear();
                listOfGroups.addAll(set);
                mArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields() {

        listView = (ListView) groupFragmentView.findViewById(R.id.list_view);
        mArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(mArrayAdapter);
    }

}
