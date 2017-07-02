package com.example.admin1.gymtracker.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin1.gymtracker.browsers.ExerciseBrowse;
import com.example.admin1.gymtracker.browsers.MemberBrowse;
import com.example.admin1.gymtracker.browsers.ObjectiveBrowse;
import com.example.admin1.gymtracker.models.Login;
import com.example.admin1.gymtracker.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static FirebaseDatabase mFirebaseDatabase;
    private String stUid;
    private FirebaseAuth mFirebaseAuth ;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Login mLogin;
    public static final int RP_SIGN_IN_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogin             = new Login();
        mFirebaseDatabase  = FirebaseDatabase.getInstance();
        mFirebaseAuth      = FirebaseAuth.getInstance();

        Button btnSignOut = (Button) findViewById(R.id.btnSignOut);
        Button btnProfile = (Button) findViewById(R.id.btnProfile);
        Button btnExercise = (Button) findViewById(R.id.btnExercise);
        Button btnObjective = (Button) findViewById(R.id.btnObjective);
        Button btnOtherMember = (Button) findViewById(R.id.btnOtherMember);

        setmAuthStateListener();
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iProfile = new Intent(getApplicationContext(), MemberEntry.class);
                iProfile.putExtra("memberId", stUid);
                iProfile.putExtra("isAdmin", false);
                startActivity(iProfile);
            }
        });

        btnExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itBrowse = new Intent(getApplicationContext(), ExerciseBrowse.class);
                startActivity(itBrowse);
            }
        });

        btnObjective.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itBrowse = new Intent(getApplicationContext(), ObjectiveBrowse.class);
                startActivity(itBrowse);
            }
        });
        btnOtherMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itBrowse = new Intent(getApplicationContext(), MemberBrowse.class);
                startActivity(itBrowse);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onPause(){
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    public void onSignedInInitialise(String uid){
        stUid      = uid;

    }

    public void onSignedOutCleanUp(){
        stUid = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RP_SIGN_IN_ID){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Signed in using " , Toast.LENGTH_SHORT).show();
            }
            else if( resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Signed in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } // if (requestCode == RC_SIGN_IN){
    } // onActivityResult Method

    private void setmAuthStateListener(){

        //Authenticatin Listener
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    onSignedInInitialise(user.getUid());
                    Toast.makeText(getApplicationContext(), "Listener: User Signed in ", Toast.LENGTH_SHORT).show();
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

    }

    private void signOut(){
        AuthUI.getInstance().signOut(this);
    }
}
