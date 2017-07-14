package com.example.admin1.gymtracker.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.browsers.ExerciseBrowse;
import com.example.admin1.gymtracker.browsers.MemberBrowse;
import com.example.admin1.gymtracker.browsers.ObjectiveBrowse;
import com.example.admin1.gymtracker.browsers.WorkoutBrowse;
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
        onSignedOutCleanUp();
    }




    protected void initialiseDatabase(){
        mFirebaseDatabase  = FirebaseDatabase.getInstance();
        mFirebaseAuth      = FirebaseAuth.getInstance();
        tableRef = mFirebaseDatabase.getReference().child("Member");
        createAuthStateListener();
    }

    private void createAuthStateListener(){

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

    }

    private void onSignedOutCleanUp(){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
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
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    protected void removeAuthStateListener(){
        if (mAuthStateListener != null) {
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
        boolean isAdmin;
        Member currentMember ;

        if (members!= null && hasProfile(uid)) {
            currentMember = members.get(uid);
            //If the user is an admin user and the current user is not deleted
            if (currentMember.getIsAdmin() && isDeletedUser(uid)) {
                isAdmin = true;
            }
            else{
                isAdmin = true;
            }
        }
        else {
            isAdmin = false;
        }
        /***** Test code ******/
        isAdmin = true;
        /* ***** Test Code ***** */
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
    }// End CreatesEventListener


    // Detaches the event listener when activity goes into background
    private void deleteEventListener(){
        if(eventListener  != null){
            tableRef.removeEventListener(eventListener);
        }
    }// End DeleteEventListener

}
