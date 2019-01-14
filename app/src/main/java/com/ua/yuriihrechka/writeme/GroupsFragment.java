package com.ua.yuriihrechka.writeme;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GroupsFragment extends Fragment {


    private View groupFragmentView;

    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<String> listOfGroups = new ArrayList<>();


    private DatabaseReference mGroupRef;

    ///
    private RecyclerView recyclerView;
    private List<GroupModel> groupList;
    private GroupAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        groupList = new ArrayList<>();

        mGroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        InitializeFields();
        retrieveAndDisplayGroup();

        adapter = new GroupAdapter(groupList);
        recyclerView.setAdapter(adapter);

        return groupFragmentView;
    }

    private void retrieveAndDisplayGroup() {

        mGroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Set<String>set = new HashSet<>();

                List<GroupModel> groupList = new ArrayList<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){

                    groupList.add(new GroupModel(iterator.next().toString()));

                }

               // listOfGroups.clear();
               // listOfGroups.addAll(set);
               // mArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields() {

        //listView = (ListView) groupFragmentView.findViewById(R.id.list_view);
        // mArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listOfGroups);
        // listView.setAdapter(mArrayAdapter);

        recyclerView = (RecyclerView) groupFragmentView.findViewById(R.id.group_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);




    }

}
