package com.example.admin1.gymtracker.activities;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.browsers.WorkoutBrowse;
import com.example.admin1.gymtracker.fragments.MessageDialog;
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

public class BaseClass extends AppCompatActivity  {
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth ;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    protected static final int RP_SIGN_IN_ID = 1;

    //Firebase Database query fields
    private DatabaseReference tableRef;
    private ValueEventListener eventListener;
    private static final String TAG = "BaseClass";
    private HashMap<String, Member> members;
    private boolean blConfirm = false;

    // Checks if the user has a profile
    protected boolean hasProfile(String uid){
        boolean hasProfile;
        Member currentMember ;
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edPref = mPref.edit();

        hasProfile = mPref.getBoolean("hasProfile", false);

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
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edPref = mPref.edit();

        if (mFirebaseDatabase == null){
            mFirebaseDatabase  = FirebaseDatabase.getInstance();
            mFirebaseAuth      = FirebaseAuth.getInstance();
        }
        if (!mPref.getBoolean("PersistentEnabled",false)) {
            // This sometimes gets called a second time if the user log out and logs back in
            //without closeing the app
            //Try catch the error.
            try {
                mFirebaseDatabase.setPersistenceEnabled(true);
            }
            catch(Exception e){
                Log.d(TAG, e.getMessage());
            }

            edPref = mPref.edit();
            edPref.putBoolean("PersistentEnabled", true);
            edPref.apply();

        }
        tableRef = mFirebaseDatabase.getReference().child("Member");
        createAuthStateListener();
        deleteBaseEventListener();
        createBaseEventListener();
    }

    protected FirebaseDatabase getmFirebaseDatabase(){
        return mFirebaseDatabase;
    }

    private void onSignedInInitialise(){
        mFirebaseAuth      = FirebaseAuth.getInstance();
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
                    //Start the home screen event
                    Intent itHomeScreen = new Intent(getApplicationContext(), WorkoutBrowse.class);
                    startActivity(itHomeScreen);
                }
            }
        };

    } //End setmAuthSateListener method

    private void onSignedOutCleanUp(){
        // Removed Shared Preferences, no longer needed.
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mPref.edit().remove("PersistentEnabled").apply();
        mPref.edit().remove("hasProfile").apply();
        deleteBaseEventListener();

    }

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
        return uId;
    }


    // Checks is the user is a Trainer
    protected boolean isAdminUser(String uid){
        boolean isAdmin;
        Member currentMember ;
        Log.d(TAG,"is AdminUser - members not null: " + (members!= null));
        if (members!= null && hasProfile(uid)) {
            currentMember = members.get(uid);
            if (currentMember == null){
                isAdmin = false;

            }
            else{
                //If the user is an admin user and the current user is not deleted
                isAdmin = (currentMember.getIsAdmin() && !isDeletedUser(uid));
            }
        }
        else {
            isAdmin = false;
        }

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
                    if (members != null && members.get(getCurrentUserId()) != null){
                        setHasProfile(true);
                    }
                    else{
                        setHasProfile(false);
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
    private void deleteBaseEventListener(){
        if(eventListener  != null){
            tableRef.removeEventListener(eventListener);
            eventListener = null;
        }
    }// End DeleteEventListener

    protected void launchBaseEventListener(){
        createBaseEventListener();
    }
    protected void destroyBaseEventListener(){
        deleteBaseEventListener();
    }

    //Shows an error message Dialog
    public void showErrorMessageDialog(String message){
        FragmentManager frmError = getFragmentManager();
        DialogFragment mFragment = MessageDialog.newInstance(R.string.error_title_bar,
                R.drawable.ic_error_24dp,
                message);

        mFragment.show(frmError, "dialog");


    }

    //Shows an warning message Dialog
    public void showWarningMessageDialog(String message){
        FragmentManager frmError = getFragmentManager();
        DialogFragment mFragment = MessageDialog.newInstance(R.string.warning_title_bar,
                R.drawable.ic_warning_24dp,
                message);

        mFragment.show(frmError, "dialog");


    }

    // Set if the user has a profile setup or not
    private void setHasProfile(boolean hasProfile){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edPref = mPref.edit();

        edPref = mPref.edit();
        edPref.putBoolean("hasProfile", hasProfile);
        edPref.apply();
    }
    protected void login(){
        // Start Login Activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                RP_SIGN_IN_ID);

    }

}
