package com.example.fitnessapp.profileFragmentTabs.DeleteAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fitnessapp.Login.MainActivity;
import com.example.fitnessapp.R;
import com.example.fitnessapp.profileFragmentTabs.editProfile;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class deleteAuthenticate extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "logoutAuthenticate";
    private EditText email, password;
    private Button deletebtn, cancelbtn;
    private Button fbreauthbtn;

    // Firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_authenticate);
        email = (EditText)findViewById(R.id.emailConfirm);
        password = (EditText)findViewById(R.id.passwordConfirm);
        deletebtn = (Button)findViewById(R.id.delete_acc_confirm_btn);
        cancelbtn = (Button)findViewById(R.id.cancel_acc_confirm_btn);

        fbreauthbtn = (Button) findViewById(R.id.facebook_reauthenticate);

        progressBar = (ProgressBar)findViewById(R.id.progressbardelete);

        // Set button onclick listeners
        deletebtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        fbreauthbtn.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId())
        {
            case R.id.delete_acc_confirm_btn:
            {
                progressBar.setVisibility(View.VISIBLE);
                if(email.getText().toString().isEmpty())
                {
                    email.setError("Field cannot be empty");
                    email.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
                {
                    email.setError("Please enter a valid email address");
                    email.requestFocus();
                    return;
                }
                if(password.getText().toString().isEmpty())
                {
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }

                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();

                AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(), password.getText().toString());
                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    // When user is re-authenticated
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated");
                        if (task.isSuccessful()) {
                            // Delete user from database
                            database = FirebaseDatabase.getInstance();
                            myRef = database.getReference("Users").child(firebaseUser.getUid());
                            // Delete user from "Users" branch
                            myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Delete user from "Workouts" branch
                                    myRef = database.getReference("Workouts").child(firebaseUser.getUid());
                                    myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Delete user from authentication
                                            firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(v.getContext(), "Account Deleted", Toast.LENGTH_LONG).show();
                                                        Intent returnlogin = new Intent(v.getContext(), MainActivity.class);
                                                        returnlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        returnlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(returnlogin);
                                                    } else {
                                                        Log.d(TAG, "failed to delete");
                                                        Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "Re-authentication failed");
                        }
                    }
                });
                break;
            }
            case R.id.cancel_acc_confirm_btn:
            {
                Intent returnEditProfile = new Intent(v.getContext(), editProfile.class);
                returnEditProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                returnEditProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(returnEditProfile);
                break;
            }
            case R.id.facebook_reauthenticate:
            {
                progressBar.setVisibility(View.VISIBLE);

                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                handleFacebookAccessToken(accessToken);
                break;
            }
        }
    }

    public void handleFacebookAccessToken(AccessToken token)
    {
        Log.d(TAG, "handleFacebookAccessToken: " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    // Delete user from database
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Users").child(firebaseUser.getUid());
                    // Delete user from "Users" branch
                    myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Delete user from "Workouts" branch
                            myRef = database.getReference("Workouts").child(firebaseUser.getUid());
                            myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Delete user from authentication
                                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);
                                                LoginManager.getInstance().logOut(); // Logout of facebook to change button on login-screen
                                                Toast.makeText(getApplicationContext(), "Account Deleted", Toast.LENGTH_LONG).show();
                                                Intent returnlogin = new Intent(getApplicationContext(), MainActivity.class);
                                                returnlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                returnlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(returnlogin);
                                            } else {
                                                Log.d(TAG, "failed to delete");
                                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
