package com.rest.fitnessapp.Login;

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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rest.fitnessapp.R;

public class  signUp extends AppCompatActivity implements View.OnClickListener {
    EditText EditTextEmail, EditTextPassword;
    private FirebaseAuth mAuth;
    private Button btnSignUp;
    private TextView login;
    private ProgressBar progressBar;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    // Facebook variables
    private LoginButton fbsignup;
    private CallbackManager callbackManager;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        EditTextEmail = findViewById(R.id.username);
        EditTextPassword = findViewById(R.id.password2);
        btnSignUp = findViewById(R.id.btn_signup);  // sign up button (main activity page)
        login = findViewById(R.id.log_in);    // login link (sign up page)
        progressBar = findViewById(R.id.progressbar_signup);

        fbsignup = findViewById(R.id.facebook_signup);
        fbsignup.setPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        btnSignUp.setOnClickListener(this);
        login.setOnClickListener(this);

        fbsignup.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError" + error);
            }
        });
    }

    private void registerUser()
    {
        progressBar.setVisibility(View.VISIBLE);

        final String email = EditTextEmail.getText().toString().trim();
        String password = EditTextPassword.getText().toString().trim();

        if(email.isEmpty())
        {
            progressBar.setVisibility(View.GONE);
            EditTextEmail.setError("Email is required");
            EditTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            progressBar.setVisibility(View.GONE);
            EditTextEmail.setError("Please enter a valid email");
            EditTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty())
        {
            progressBar.setVisibility(View.GONE);
            EditTextPassword.setError("Password is required");
            EditTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6)
        {
            progressBar.setVisibility(View.GONE);
            EditTextPassword.setError("Password must be at least 6 characters");
            EditTextPassword.requestFocus();
            return;
        }

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
                            // Add user to database
                            User signedInUser = new User(email);
                            myRef = database.getReference("Users"); // Get reference in database (Users path)
                            myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(signedInUser);
                        } else
                            {
                            // If sign up fails, display a message to the user.
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

    public void handleFacebookToken(AccessToken token)
    {
        Log.d(TAG, "handleFacebookToken" + token);

        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Facebook sign-in successful");
                    Intent returnLogin = new Intent(signUp.this, MainActivity.class);
                    //getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   // Clear all tasks on top of task (prevent hitting back button to go back to login)
                    startActivity(returnLogin);
                    //FirebaseUser user = mAuth.getCurrentUser();
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Facebook log-in failed", task.getException());
                    Toast.makeText(signUp.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
