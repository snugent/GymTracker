package com.example.admin1.gymtracker.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
    private static FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth ;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RP_SIGN_IN_ID = 1;

    //Firebase Database query fields
    private DatabaseReference tableRef;
    private ValueEventListener eventListener;
    private static final String TAG = "BaseClass";
    private HashMap<String, Member> members;

    // Checks if the user has a profile
    protected boolean hasProfile(String uid){
        boolean hasProfile;
        Member currentMember ;

        if (members!= null) {
            currentMember = members.get(uid);
            hasProfile = (currentMember != null);
        }
        else {
            hasProfile = false;
        }
        return hasProfile;
    }

    // Gets the current user
    protected FirebaseUser getUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    //Signs the user out of the system
    protected void signOut(){
        AuthUI.getInstance().signOut(this);
    }

    protected void initialiseDatabase(){
        Log.d(TAG,"initialiseDatabase: " + isAdminUser(getCurrentUserId()));
        Log.d(TAG,"initialiseDatabase 2: " + mFirebaseDatabase);
        if (mFirebaseDatabase == null){
            mFirebaseDatabase  = FirebaseDatabase.getInstance();
            mFirebaseAuth      = FirebaseAuth.getInstance();
            mFirebaseDatabase.setPersistenceEnabled(true);
        }
        tableRef = mFirebaseDatabase.getReference().child("Member");
        createAuthStateListener();
        deleteBaseEventListener();
        createBaseEventListener();
    }

    protected FirebaseDatabase getmFirebaseDatabase(){
        return mFirebaseDatabase;
    }

    private void createAuthStateListener(){

        //Authenticatin Listener
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = getUser();
                if (user != null){
                    onSignedInInitialise();
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

    private void onSignedInInitialise(){
        mFirebaseAuth      = FirebaseAuth.getInstance();
        createBaseEventListener();
    }

    private void onSignedOutCleanUp(){
        mFirebaseAuth = null;
        deleteBaseEventListener();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RP_SIGN_IN_ID){
            if(resultCode == RESULT_OK){
                //To do
            }
            else if( resultCode == RESULT_CANCELED){
                Log.e(TAG, "Signed in cancelled");
                finish();
            }
        } // if (requestCode == RC_SIGN_IN){
    } // onActivityResult Method

    protected void setAuthStateListener(){
        if (mFirebaseAuth != null){
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }

    }

    protected void removeAuthStateListener(){
        if (mAuthStateListener != null && mFirebaseAuth != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    protected String getCurrentUserId(){
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
    protected boolean isAdminUser(String uid){
        boolean isAdmin = false;
        Member currentMember ;
        Log.d(TAG,"members not null: " + (members!= null));
        if (members!= null && hasProfile(uid)) {
            currentMember = members.get(uid);
            //If the user is an admin user and the current user is not deleted
            if (currentMember.getIsAdmin() && !isDeletedUser(uid)) {
                isAdmin = true;
            }
            else{
                isAdmin = true;
            }
        }
        else {
            isAdmin = false;
        }
        /* test code */
        // return true;
        /* *** test code */
        Log.d(TAG,"setisAdmin: " + isAdmin);
        return isAdmin;
    }

    // Checks is the user is a Trainer
    protected boolean isDeletedUser(String uid){
        boolean isDeleted = false;
        Member currentMember ;

        if (members!= null) {
            currentMember = members.get(uid);
            isDeleted = currentMember.getIsDeleted();
        }
        return isDeleted;
    }

    // Creates an event listener for when we change data
    private void createBaseEventListener(){

        if(eventListener == null) {
            ValueEventListener mEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    members = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Member mMember = child.getValue(Member.class);
                        members.put(child.getKey(), mMember);
                    }
                    isAdminUser(getCurrentUserId());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableRef.addValueEventListener(mEventListener);
            eventListener = mEventListener;
        } // End if eventListener == null
    }// End CreatesEventListener


    // Detaches the event listener when activity goes into background
    private void deleteBaseEventListener(){
        if(eventListener  != null){
            tableRef.removeEventListener(eventListener);
        }
    }// End DeleteEventListener
}
