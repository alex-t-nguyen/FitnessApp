package com.example.fitnessapp.profileFragmentTabs.changePassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fitnessapp.R;
import com.example.fitnessapp.homeFragments.Home;
import com.example.fitnessapp.homeFragments.profile_fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class change_pass_authenticate extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "changePassAuthenticate";

    private EditText emailConfirm, passwordConfirm;
    private Button submitConfirm, cancelConfirm;
    private ProgressBar progressBar;

    // Firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_authenticate);

        // Instantiate widgets
        emailConfirm = (EditText)findViewById(R.id.emailConfirm_forPassword);
        passwordConfirm = (EditText)findViewById(R.id.passwordConfirm_forPassword);
        submitConfirm = (Button)findViewById(R.id.submit_changepass_btn);
        cancelConfirm = (Button)findViewById(R.id.cancel_changepass_btn);
        progressBar = (ProgressBar)findViewById(R.id.progressbar_passAuthenticate);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        submitConfirm.setOnClickListener(this);
        cancelConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId())
        {
            case R.id.submit_changepass_btn:
            {
                progressBar.setVisibility(View.VISIBLE);

                if(emailConfirm.getText().toString().isEmpty())
                {
                    emailConfirm.setError("Field cannot be empty");
                    emailConfirm.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(passwordConfirm.getText().toString().isEmpty())
                {
                    passwordConfirm.setError("Field cannot be empty");
                    passwordConfirm.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(emailConfirm.getText().toString()).matches())
                {
                    emailConfirm.setError("Please enter a valid email address");
                    emailConfirm.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                AuthCredential credential = EmailAuthProvider.getCredential(emailConfirm.getText().toString(), passwordConfirm.getText().toString());
                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            progressBar.setVisibility(View.GONE);
                            Intent changePasswordIntent = new Intent(v.getContext(), change_password.class);
                            startActivity(changePasswordIntent);
                            finish();
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                break;
            }
            case R.id.cancel_changepass_btn:
            {
                //Intent returnProfileFragment = new Intent(v.getContext(), Home.class);
                //returnProfileFragment.putExtra("profileFragmentLoad", "profileFragment");
                //startActivity(returnProfileFragment);
                finish();
                break;
            }
        }
    }
}
