package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class signUp extends AppCompatActivity implements View.OnClickListener {
    EditText EditTextEmail, EditTextPassword;
    private FirebaseAuth mAuth;
    private Button btnSignUp;
    private TextView login;
    private ProgressBar progressBar;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        EditTextEmail = (EditText)findViewById(R.id.username);
        EditTextPassword = (EditText)findViewById(R.id.password2);
        btnSignUp = (Button)findViewById(R.id.btn_signup);  // sign up button (main activity page)
        login = (TextView)findViewById(R.id.log_in);    // login link (sign up page)
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(this);
        login.setOnClickListener(this);

    }

    private void registerUser()
    {

        String email = EditTextEmail.getText().toString().trim();
        String password = EditTextPassword.getText().toString().trim();

        if(email.isEmpty())
        {
            EditTextEmail.setError("Email is required");
            EditTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            EditTextEmail.setError("Please enter a valid email");
            EditTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty())
        {
            EditTextPassword.setError("Password is required");
            EditTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6)
        {
            EditTextPassword.setError("Password must be at least 6 characters");
            EditTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(signUp.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent openApp = new Intent(".Home");
                            startActivity(openApp);

                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            if(task.getException() instanceof FirebaseAuthUserCollisionException)    // If user already exists
                            {
                                Toast.makeText(getApplicationContext(), "The email entered already exists", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(signUp.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }

    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.log_in:
            {
                finish();
                Intent returnLogin = new Intent(this, MainActivity.class);
                //getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   // Clear all tasks on top of task (prevent hitting back button to go back to login)
                startActivity(returnLogin);
                break;
            }
            case R.id.btn_signup:   // registerUser function
            {
                registerUser();
                break;
            }
        }
    }
}
