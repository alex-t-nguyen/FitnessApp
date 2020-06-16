package com.example.fitnessapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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
import android.widget.Toolbar;

import com.example.fitnessapp.R;
import com.example.fitnessapp.homeFragments.Home;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText username;
    private EditText password;
    private Button button_login;
    private TextView signUp, forgotpassbtn, forgotPassText, forgotPassSignUpBtn;
    Button popupSendEmailbtn;
    private LoginButton fbLogin;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CallbackManager mCallbackManager;
    private static final String TAG = "FacebookAuthentication";

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private ProgressBar progressBar;

    private Dialog popupForgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_login = (Button)findViewById(R.id.btn_login);
        signUp = (TextView)findViewById(R.id.sign_up);
        forgotpassbtn = (TextView)findViewById(R.id.forgot_password);

        button_login.setOnClickListener(this);
        signUp.setOnClickListener(this);
        forgotpassbtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password2);

        fbLogin = (LoginButton)findViewById(R.id.facebook_login);
        fbLogin.setPermissions("email", "public_profile");
        mCallbackManager = CallbackManager.Factory.create();

        progressBar = (ProgressBar)findViewById(R.id.progressbar);

        fbLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError" + error);
                updateUI(null);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    updateUI(user);
                }
                else
                {
                    updateUI(null);
                }

            }
        };

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    mAuth.signOut();
                    LoginManager.getInstance().logOut();
                }
            }
        };

    }

    public void handleFacebookToken(AccessToken token)
    {
        Log.d(TAG, "handleFacebookToken" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);

                    Log.d(TAG, "Facebook sign-in successful");
                    FirebaseUser user = mAuth.getCurrentUser();
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Users"); // Get reference in database (Users path)
                    try {
                        for (UserInfo userInfo : user.getProviderData()) {
                            if (userInfo.getEmail() != null) {
                                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(userInfo.getEmail()));
                                break;
                            }
                        }
                    }
                    catch (NullPointerException ex)
                    {
                        Log.d(TAG, "NullPointerException: " + ex.getMessage());
                    }
                    updateUI(user);
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Facebook log-in failed", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void updateUI(FirebaseUser user)
    {
        Intent homeIntent;
        if(user != null) {
            Toast.makeText(MainActivity.this, "User logged In", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            homeIntent = new Intent(MainActivity.this, Home.class);
            startActivity(homeIntent);
            finish();
        }
        else
        {
            Log.d(TAG, "user is null");
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        //mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    public void userLogin()
    {
        progressBar.setVisibility(View.VISIBLE);

        final String email = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(email.isEmpty())
        {
            progressBar.setVisibility(View.GONE);
            username.setError("Email is required");
            username.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            progressBar.setVisibility(View.GONE);
            username.setError("Please enter a valid email");
            username.requestFocus();
            return;
        }
        if(pass.isEmpty())
        {
            progressBar.setVisibility(View.GONE);
            password.setError("Password is required");
            password.requestFocus();
            return;
        }
        if(pass.length() < 6)
        {
            progressBar.setVisibility(View.GONE);
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);

                    Intent homeActivity = new Intent(getApplicationContext(), Home.class);
                    getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   // Clear all tasks on top of task (prevent hitting back button to go back to login)
                    User user = new User(email);
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Users"); // Get reference in database (Users path)
                    myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);   // Assign user id in User path, then user's email to the ID

                    startActivity(homeActivity);
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.btn_login:
            {
                userLogin();
                break;
            }
            case R.id.sign_up:
            {
                Intent signUpSuccess = new Intent(MainActivity.this, signUp.class);
                startActivity(signUpSuccess);
                finish();
                break;
            }
            case R.id.forgot_password:
            {
                Log.d(TAG, "Clicked forgot password");
                popupForgotPass = new Dialog(this);
                popupForgotPass.setContentView(R.layout.forgot_password_popup);

                popupForgotPass.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);

                forgotPassText = popupForgotPass.findViewById(R.id.forgot_pass_text);
                popupSendEmailbtn = popupForgotPass.findViewById(R.id.send_pass_email_btn);

                forgotPassSignUpBtn = popupForgotPass.findViewById(R.id.forgot_pass_signup);

                popupSendEmailbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String email = forgotPassText.getText().toString();
                        progressBar.setVisibility(View.VISIBLE);

                        // Email validation
                        if (email.isEmpty())
                        {
                            progressBar.setVisibility(View.GONE);
                            forgotPassText.setError("Field cannot be empty");
                            forgotPassText.requestFocus();
                            return;
                        }
                        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                        {
                            progressBar.setVisibility(View.GONE);
                            username.setError("Please enter a valid email");
                            username.requestFocus();
                            return;
                        }

                        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Password reset link sent", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        popupForgotPass.dismiss();
                    }
                });
                forgotPassSignUpBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signUpIntent = new Intent(MainActivity.this, signUp.class);
                        startActivity(signUpIntent);
                        finish();
                    }
                });
                popupForgotPass.show();
                break;
            }
        }
    }
}
