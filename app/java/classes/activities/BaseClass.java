package com.example.admin1.gymtracker.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.browsers.ExerciseBrowse;
import com.example.admin1.gymtracker.browsers.MemberBrowse;
import com.example.admin1.gymtracker.browsers.ObjectiveBrowse;
import com.example.admin1.gymtracker.models.Login;
import com.example.admin1.gymtracker.models.Member;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by admin1 on 02/07/2017.
 */

public class BaseClass extends AppCompatActivity {
    private Menu     menu;
    private static FirebaseDatabase mFirebaseDatabase;
    private String stUid;
    private FirebaseAuth mFirebaseAuth ;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Login mLogin;
    private static final int RP_SIGN_IN_ID = 1;

    //Firebase Database query fields
    DatabaseReference tableRef;
    private ValueEventListener eventListener;
    private static final String TAG = "BaseClass";
    private HashMap<String, Member> members;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.profile:
                launchProfileActivity();
                return true;
            case R.id.exercise:
                launchExerciseActivity();
                return true;
            case R.id.objective:
                launchObjectiveActivity();
                return true;
            case R.id.edit_member:
                launchMemberActivity();
            case R.id.sign_out:
                signOut();
                return true;
        }
        return true;
    }

    // Checks if the user has a profile
    protected boolean hasProfile(){
        return true;
    }

    // Gets the current user
    protected FirebaseUser getUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    //Signs the user out of the system
    protected void signOut(){
        AuthUI.getInstance().signOut(this);
    }

    // Calls the User Profile Maintenance Screen
    private void launchProfileActivity(){
        Intent iProfile = new Intent(getApplicationContext(), MemberEntry.class);
        iProfile.putExtra("memberId", getCurrentUserId());
        iProfile.putExtra("isAdmin", isAdminUser());
        startActivity(iProfile);
    }

    // Calls the Exercise Maintenance Screen
    private void launchExerciseActivity(){
        Intent itBrowse = new Intent(getApplicationContext(), ExerciseBrowse.class);
        startActivity(itBrowse);
    }

    // Calls the Objective Maintenance Screen
    private void launchObjectiveActivity() {
        Intent itBrowse = new Intent(getApplicationContext(), ObjectiveBrowse.class);
        startActivity(itBrowse);
    }

    //Calls the Member screen to allow trainers (admin members) delete other users
    // Promote and delete Member functionality
    private void launchMemberActivity() {
        Intent itBrowse = new Intent(getApplicationContext(), MemberBrowse.class);
        startActivity(itBrowse);
    }


    protected void initialiseDatabase(){
        mLogin             = new Login();
        mFirebaseDatabase  = FirebaseDatabase.getInstance();
        mFirebaseAuth      = FirebaseAuth.getInstance();
        setmAuthStateListener();
    }

    private void setmAuthStateListener(){

        //Authenticatin Listener
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = getUser();
                if (user != null){
                    onSignedInInitialise(user.getUid());
                }
                else{
                    onSignedOutCleanUp();
                    // Start Login Activity
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                                    .build(),
                            RP_SIGN_IN_ID);
                }
            }
        };

    } //End setmAuthSateListener method

    private void onSignedInInitialise(String uid){
        stUid      = getCurrentUserId();

    }

    private void onSignedOutCleanUp(){
        stUid = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RP_SIGN_IN_ID){
            if(resultCode == RESULT_OK){
            }
            else if( resultCode == RESULT_CANCELED){
                Log.e(TAG, "Signed in cancelled");
                finish();
            }
        } // if (requestCode == RC_SIGN_IN){
    } // onActivityResult Method

    protected void createAuthStateListener(){
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    protected void deleteAuthStateListener(){
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    public String getCurrentUserId(){
        FirebaseUser user = getUser();
        String uId ="";
        if (user != null) {
            uId = user.getUid();
        }
        else {
            signOut();
        }
        return uId;
    }

    // Checks is the user is a Trainer
    public boolean isAdminUser(){
        boolean isAdmin = false;
        tableRef = mFirebaseDatabase.getReference().child("Member");
        Member currentMember ;

        if (members!= null) {
            currentMember = members.get(getCurrentUserId());
            if (!currentMember.getIsAdmin() || currentMember.getIsDeleted()){
                isAdmin = false;
            }
            else{
                isAdmin = true;
            }
        }
        else {
            isAdmin = false;
        }
        return isAdmin;
    }

    // Checks is the user is a Trainer
    public boolean isDeletedUser(){
        boolean isDeleted = false;
        tableRef = mFirebaseDatabase.getReference().child("Member");
        Member currentMember ;

        if (members!= null) {
            currentMember = members.get(getCurrentUserId());
            isDeleted = currentMember.getIsDeleted();
        }
        return isDeleted;
    }

    // Creates an event listener for when we change data
    private void createEventListener(){

        if(eventListener == null) {
            ValueEventListener mEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    members = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Member mMember = child.getValue(Member.class);
                        members.put(child.getKey(), mMember);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableRef.addValueEventListener(mEventListener);
            eventListener = mEventListener;
        } // End if eventListener == null

    }

}