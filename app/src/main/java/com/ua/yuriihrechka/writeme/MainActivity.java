package com.ua.yuriihrechka.writeme;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAccessorAdapter mTabsAccessorAdapter;


    // Firebase
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WriteMe");

        mViewPager = (ViewPager)findViewById(R.id.main_tabs_pager);
        mTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAccessorAdapter);

        mTabLayout = (TabLayout)findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null){
            sendUserToLoginActivity();
        }else {

            updataStatus("online");
            VerifyUserExistance();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (currentUser != null){
            updataStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (currentUser != null){
            updataStatus("offline");
        }
    }

    private void VerifyUserExistance() {

        String currentUserID = mAuth.getCurrentUser().getUid();
        mRootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("name").exists()){

                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();

                }else {
                    sendUserToSettingsActivity();
                   // Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option){
            mAuth.signOut();
            sendUserToLoginActivity();
        }

        if (item.getItemId() == R.id.main_settings_option){
            sendUserToSettingsActivity();
        }

        if (item.getItemId() == R.id.main_create_group_option){
            RequestNewGroup();
        }

        if (item.getItemId() == R.id.main_find_friends_option){
            sendUserToFindFriendsActivity();
        }

        return true;
    }

    private void RequestNewGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter name:");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g. test");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Write group name", Toast.LENGTH_SHORT).show();
                }else {
                    CreateNewGroup(groupName);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        builder.show();

    }

    private void CreateNewGroup(final String groupName) {

        mRootRef.child("Groups").child(groupName).setValue("")
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, groupName +  " group is create.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendUserToSettingsActivity() {

        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        //settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        //finish();
    }

    private void sendUserToFindFriendsActivity() {

        Intent findFriendsAIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        //findFriendsAIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(findFriendsAIntent);
        //finish();
    }


    private void updataStatus(String status){

        String saveCurrentDate, saveCurrentTime;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentTime = currentDate.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("status", status);

        currentUserID = mAuth.getCurrentUser().getUid();

        mRootRef.child("Users").child(currentUserID)
                .child("user_state")
                .updateChildren(onlineStateMap);



    }
}
