package com.example.admin1.gymtracker.models;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

/**
 * Created by admin1 on 24/06/2017.
 * Handles Firebase Login Code
 */

public class Login extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RP_SIGN_IN_ID = 1;
    private String stUid;

    public Login() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        setmFirebaseAuth();
    }


    public FirebaseAuth.AuthStateListener getmAuthStateListener() {
        return mAuthStateListener;
    }

    public void setmFirebaseAuth() {
        Log.d("Login", "In setmFirebaseAuth");
        /* Authenticaton Listener*/
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    //user signed in
                    setStUid(user.getUid());
                    Log.d("Login", "uid != null" + user.getUid());
                }
                else {
                    //user signed out
                    setStUid(null);
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                                    .build(),
                            RP_SIGN_IN_ID);
                    Log.d("Login", "uid == null" + user.getUid());
                }
            }
        };
    }

    public FirebaseAuth getmFirebaseAuth() {
        return mFirebaseAuth;
    }


    public String getStUid() {
        return stUid;
    }

    public void setStUid(String stUid) {
        this.stUid = stUid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RP_SIGN_IN_ID){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Signed in ", Toast.LENGTH_SHORT).show();
            }
            else if( resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Signed in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } // if (requestCode == RC_SIGN_IN)

    } // onActivityResult Method

}
