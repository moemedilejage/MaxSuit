package com.imegga.suitcase;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    private Button signupButton;
    private Button alreadyUserButton;

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /**
         * Initialize Firebase Authorization
         */
        firebaseAuth = FirebaseAuth.getInstance();

        //Assign the components to their xml components
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        signupButton = findViewById(R.id.buttonRegister);
        alreadyUserButton = findViewById(R.id.buttonExistingUser);

        /**
         * SignUp Button onClick Listener
         */
        signupButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                //Upon successful login, do this
                createUserWithEmailAndPassword(email, password);
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /**
         * Existing user Button onClick Listener
         */
        alreadyUserButton.setOnClickListener(view -> {
            // Launch the SignupActivity
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Method to create a user with Email and Password
     */
    private void createUserWithEmailAndPassword(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "createUserWithEmail:success");
                Toast.makeText(SignupActivity.this, "User registration successful.", Toast.LENGTH_SHORT).show();
                // You can do something here, like moving the user to the next activity
            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                Toast.makeText(SignupActivity.this, "User registration failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}