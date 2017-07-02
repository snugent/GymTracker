package com.example.admin1.gymtracker.activities;

import android.os.Bundle;

import com.example.admin1.gymtracker.R;

public class MainActivity extends BaseClass {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Sets up the database in Base class for later use
        initialiseDatabase();


    }

    @Override
    protected void onResume(){
        super.onResume();
        createAuthStateListener();

    }
    @Override
    protected void onPause(){
        super.onPause();
        deleteAuthStateListener();
    }

}
