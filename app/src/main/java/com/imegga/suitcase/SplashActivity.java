package com.imegga.suitcase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        //setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                firebaseAuth = FirebaseAuth.getInstance();

                //Check if user is signed in and update UI accordingly
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    //User is already signed in
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    /**
                     * Close the current activity to prevent the
                     * user from coming back to the login screen with the back button
                     */

                } else {
                    // User is not signed in, redirect to the login activity or any other necessary actions
                    startActivity(new Intent(SplashActivity.this, SignupActivity.class));
                    finish();
                    /**
                     * Close the current activity to prevent the
                     * user from coming back to the splash screen with the back button
                     */
                }
            }
        }, 200);
    }
}